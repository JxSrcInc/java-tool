package jxsource.net.httpproxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import jxsource.net.httpproxy.entity.DefaultDestinationOutputStream;
import jxsource.net.httpproxy.entity.EntityDestinationOutputStream;
import jxsource.net.httpproxy.entity.EntityDestinationSocketManager;
import jxsource.net.httpproxy.entity.EntityProcessor;
import jxsource.net.httpproxy.entity.EntityStatus;
import jxsource.net.httpproxy.exception.EntityException;
import jxsource.net.httpproxy.exception.ProxyClientError;
import jxsource.net.httpproxy.exception.RemoteServerError;
import jxsource.net.httpproxy.exception.RestartTransactionSignal;
import jxsource.net.httpproxy.exception.TransactionException;
import jxsource.net.httpproxy.io.HttpInputStream;
import jxsource.net.httpproxy.socket.SocketAccessor;
import jxsource.net.httpproxy.socket.SocketAccessorManager;
import jxsource.net.httpproxy.trace.Log;
import jxsource.net.httpproxy.trace.TransferTrace;
import jxsource.net.httpproxy.trace.TransferWatcher;

public class ProxyTransferProcessorImpl implements ProxyTransferProcessor {
	
	private static Logger logger = Logger.getLogger(ProxyTransferProcessorImpl.class);
	private HttpUtils httpUtils = new HttpUtils();
	private HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	private Socket remoteServerSocket;
	private Socket clientSocket;
	protected volatile SocketAccessorManager socketAccessorManager = Config.getInstance().getControllerManager();
	protected volatile SocketAccessor socketAccessor;
	private RequestInfoFilter clientToServerRequestInfoConverter = Config.getInstance()
			.getProxyCleintToRemoteServerRequestInfoFilter();
	private ResponseInfoFilter remoteServerToProxyCleintResponseInfoFilter = Config.getInstance()
			.getRemoteServerToProxyCleintResponseInfoFilter();

	private RequestInfo receivedRequestInfo; // for proxy client
	private RequestInfo toSendRequestInfo; // for remote server - client or
											// status 2xx, 3xx
	private ResponseInfo receivedResponseInfo; // for remote server
	private ResponseInfo toSendResponseInfo; // for proxy client

	private TransferTrace trace;

	private boolean flagRemoteSocketError;
	private boolean flagStopTransactionLoop;

	private String state;
	// let's have a single class level point
	private InputStream remoteServerInputStream;
	private InputStream clientInputStream;
	private UrlInfo prevUrlInfo;
	private int count;
	
	private InputStream createHttpInputStream(InputStream in) {
		// TODO: HttpInputStream does not work. 
		// work for header
		// but when read body, underline src property eof becomes true 
		// which causes read(byte[]) returns -1; why ?????
		return in; //new HttpInputStream(in);
	}
	// new method added for
	// 1. re-using this object;
	// 2. 
	public void reset() {
		receivedRequestInfo = null;
		toSendRequestInfo = null;
		resetTransaction();
		flagRemoteSocketError = false;
		flagStopTransactionLoop = false;
	}
	private void resetTransaction() {
		receivedResponseInfo = null;
		toSendResponseInfo = null;
	}
	
	private void sendRequestToRemoteServer(RequestInfo requestInfo) throws IOException, HttpException {
		// get remote socket
		UrlInfo urlInfo = requestInfo.getUrlInfo();
		// Using url to find connected remote socket
		// or create remote socket and connect to remote server
		// throws RuntimeException if creation or connection fails to let
		// WorkThread terminating thread.
		state = ProxyTransferProcessor.ConnectRemoteServer;
		socketAccessor = socketAccessorManager.getController(urlInfo);
		socketAccessor.setUrlInfo(urlInfo);
//		System.err.println("> "+Thread.currentThread().getName()+": "+(++count)+" "+urlInfo);
//		System.err.println("< "+Thread.currentThread().getName()+": "+(count)+" "+socketAccessor.getUrlInfo());
		
		remoteServerSocket = socketAccessor.getSocket();// urlInfo.getHostname(),
														// urlInfo.getPort());

		// remoteServerSocket = remoteSocketManager.getSocket(urlInfo);
		// send request to remote server
		httpUtils.outputRequest(toSendRequestInfo.getRequest(), remoteServerSocket.getOutputStream());
		if ("POST".equals(requestInfo.getRequest().getRequestLine().getMethod().toUpperCase())) {
			uploadContent();
		} else
		if (hasEntity(requestInfo.getRequest())) {
			uploadContent();
		} 
			
	}

	/*
	 * Any exception throws from this method will cause WorkThread to terminate
	 * the current thread.
	 */
	public void proc(Socket clientSocket) {
		try {
			state = ProxyTransferProcessor.WaitClientCall;
			// TODO: use HttpInputStream
			// create here, used in full transaction
			clientInputStream = createHttpInputStream(clientSocket.getInputStream());
			boolean inputReady = false;
			for(int i=0; i<2; i++) {
				if(clientInputStream.available() == 0) {
					Thread.sleep(10);
				} else {
					inputReady = true;
				}
			}
			if(!inputReady) {
				throw new RuntimeException("Client socket time out.");
			}
			receivedRequestInfo = httpUtils.createRequestInfo(clientInputStream);
			System.err.println("** "+receivedRequestInfo.getUrlInfo().getUri());
/*			if(prevUrlInfo != null) {
				if(!prevUrlInfo.equals(receivedRequestInfo.getUrlInfo())){
					System.err.println("> "+Thread.currentThread().getName()+": "+(++count)+" "+prevUrlInfo);
					System.err.println("< "+Thread.currentThread().getName()+": "+(count)+" "+receivedRequestInfo.getUrlInfo());
				}
				
			}
	*/	} catch (Exception e) {
			throw new ProxyClientError(e);
		}
		trace = new TransferTrace(this);
		TransferWatcher.getInstance().add(trace);
		this.clientSocket = clientSocket;
		// update remote request
		toSendRequestInfo = clientToServerRequestInfoConverter.filter(receivedRequestInfo);
		// Allow re-submit proxy-client request in case failing to process
		// remote-server response
		flagStopTransactionLoop = false;
		for (int k = 0; k < 3 && !flagStopTransactionLoop; k++) {
			// use try/catch to complete normal and abnormal transaction
			// START_TRY_TRANSACTION
			resetTransaction();
			try {
				// boolean responseReceived = false;
				// while (!responseReceived) {
				// Allow reconnect
				// Because remote server may close connection.
				// So the first connection may fail.
				for (int i = 0; i < 2; i++) {
					try {
						flagRemoteSocketError = false;
						sendRequestToRemoteServer(toSendRequestInfo);
						break;
					} catch (Exception e) {
						flagRemoteSocketError = true;
						trace.add(new Log(state+": Remote server connection error", e));
						if (i == 1) {
							// back to START_TRY_TRANSACTION
							throw new RestartTransactionSignal();
						} else {
							// try again
							remoteServerSocket = socketAccessor.reconnectSocket();
							// reconnection fails
							// the IOException throwed from reconnectSocket()
							// call goes up
							// and stops the current transaction
						}
					}
				}
				// get remote response header
				state = ProxyTransferProcessor.WaitRemoteResponse;
				receivedResponseInfo = null;
				try {
					// TODO: use HttpInputStream
					remoteServerInputStream = createHttpInputStream(remoteServerSocket.getInputStream());
					HttpResponse remoteServerResponse = httpUtils.getHttpResponse(remoteServerInputStream);
					receivedResponseInfo = new ResponseInfo(remoteServerResponse);
				} catch (Exception e) {
					flagRemoteSocketError = true;
					trace.add(new Log(state+": Error when getting response from remote server.", e));
					// back to START_TRY_TRANSACTION
					throw new RestartTransactionSignal();
				}
				// if response status is 2xx or 3xx, create new request
				int statusCode = receivedResponseInfo.getStatusCode();
				switch (statusCode) {
				case 204:
				case 302:
				case 304:
					// TODO: create new connection .....
					// remoteSocketManager.releaseSocket(remoteServerSocket);
					// create a new toSendRequestInfo and restart another remote
					// request
					// back to START_TRY_TRANSACTION
					// break;
				default:
					// responseReceived = true;
				}
				// } // end while
				// send response header to client
				toSendResponseInfo = remoteServerToProxyCleintResponseInfoFilter.filter(receivedResponseInfo);
				// try {
				httpUtils.outputResponse(toSendResponseInfo.getResponse(), clientSocket.getOutputStream());
				if (receivedResponseInfo.getStatusCode() == 200 && this.hasEntity(receivedResponseInfo.getResponse())) {
					state = ProxyTransferProcessor.ProcessEntity;
					// if remote response contains content, pass it to
					// client
					transferContent();
				}
				// TODO handle connection close
				HttpHeaderUtils headerUtils = new HttpHeaderUtils();
				headerUtils.setHttpMessage(this.receivedResponseInfo.getResponse());
				if (headerUtils.hasHeaderWithValue("Connection", "close")) {
					// force WorkThread close
					throw new TransactionException(toString(), trace);
				} else {
					// stop this transaction and let WorkThread to start next
					// Transaction
					flagStopTransactionLoop = true;
				}
			} catch (EntityException e) {
				trace.add(new Log(state+": Transaction failed.", e));
				trace.complate(TransferTrace.FAIL);
				// force WorkThread to close
				throw new TransactionException(toString(), trace);
			} catch (TransactionException e) {
				// force WorkThread to close
				trace.complate(TransferTrace.FAIL);
				throw e;
			} catch (Exception e) {
				trace.add(new Log(state+": Transaction failed.", e));
				// try resend request
				flagRemoteSocketError = true;
			} finally {
				if (socketAccessor != null) {
					socketAccessor.releaseSocket();
					remoteServerSocket = null;
					if (flagRemoteSocketError) {
						socketAccessorManager.removeController(toSendRequestInfo.getUrlInfo());
					}
					socketAccessor = null;
				}
				prevUrlInfo = this.receivedRequestInfo.getUrlInfo();
			}
		} // end transaction loop
		if (!flagStopTransactionLoop) {
			printStatus();
			trace.complate(TransferTrace.FAIL);
			throw new TransactionException(toString(), trace);
		} else {
			trace.complate(TransferTrace.SUCCESS);			
		}
	}

	private boolean hasEntity(HttpMessage message) {
		headerUtils.setHttpMessage(message);
		return headerUtils.getContentLength() > 0 || headerUtils.hasHeaderWithValue("Transfer-Encoding", "chunked");
	}

	@Override
	public String toString() {
		return "Transaction Info:\n\t" 
				+ "Request(client): " + (receivedRequestInfo==null?"null":receivedRequestInfo.getRequest()) + "\n\t"
				+ "Request(final): " + (toSendRequestInfo==null?"null":toSendRequestInfo.getRequest()) + "\n\t" + "Response(final): "
				+ (receivedResponseInfo == null ? "null" : receivedResponseInfo.getResponse());
	}

	public void printStatus() {
		String message = Thread.currentThread().getName() + ": " + toString() + "\n\t" +
				trace.getLogStr();

		if (flagStopTransactionLoop) {
			System.out.println(message);
		} else {
			System.err.println(message);
		}
	}

	private void uploadContent() throws IOException {
		System.err.println("Upload: "+toSendRequestInfo.getRequest());
		EntityDestinationOutputStream edc = new DefaultDestinationOutputStream();
		edc.setOutputStream(this.remoteServerSocket.getOutputStream());
		// TODO: use HttpInputStream
//		InputStream clientIntputStream = createHttpInputStream(clientSocket.getInputStream());
		EntityStatus entityStatus = Config.getInstance().getEntityProcessor()
				.processEntity(this.toSendRequestInfo.getRequest(), clientInputStream, edc);
		// logger.debug("upload status: " + entityStatus);
	}

	private void transferContent() {
		// process response message body
		EntityDestinationSocketManager edsm = Config.getInstance().getEntityDestinationSocketManager();
		EntityDestinationOutputStream edc = edsm.getEntityDestinationSocket(this.receivedRequestInfo.getRequest(),
				this.receivedResponseInfo.getResponse(), socketAccessor);
		// prepare remote response input stream and client output stream
		try {
			edc.setOutputStream(this.clientSocket.getOutputStream());
			// TODO: use HttpInputStream 
//			remoteServerInputStream = createHttpInputStream(remoteServerSocket.getInputStream());
		} catch (IOException ioe) {
			throw new EntityException("Error when getting input/output stream for processng entity.", ioe);
		}
		// process entity
		EntityStatus entityStatus = null;
		try {
			entityStatus = Config.getInstance().getEntityProcessor()
				.processEntity(receivedResponseInfo.getResponse(), remoteServerInputStream, edc);
		} catch(Throwable e) {
			// catch all RuntimeException
			throw new EntityException("Error when processing entity", e);
		}
		// logger.debug("download status: " + entityStatus);
		if (!entityStatus.getStatus().equals(EntityProcessor.EntityComplete)) {
			throw new EntityException("Transaction not complete: status: " + entityStatus);
		}
	}

	public Socket getRemoteServerSocket() {
		return remoteServerSocket;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public SocketAccessorManager getSocketAccessorManager() {
		return socketAccessorManager;
	}

	public SocketAccessor getSocketAccessor() {
		return socketAccessor;
	}

	public RequestInfo getReceivedRequestInfo() {
		return receivedRequestInfo;
	}

	public RequestInfo getToSendRequestInfo() {
		return toSendRequestInfo;
	}

	public ResponseInfo getReceivedResponseInfo() {
		return receivedResponseInfo;
	}

	public ResponseInfo getToSendResponseInfo() {
		return toSendResponseInfo;
	}

	public TransferTrace getTrace() {
		return trace;
	}

	public boolean isFlagRemoteSocketError() {
		return flagRemoteSocketError;
	}

	public void setFlagRemoteSocketError(boolean flagRemoteSocketError) {
		this.flagRemoteSocketError = flagRemoteSocketError;
	}

	public boolean isFlagStopTransactionLoop() {
		return flagStopTransactionLoop;
	}

	public void setFlagStopTransactionLoop(boolean flagStopTransactionLoop) {
		this.flagStopTransactionLoop = flagStopTransactionLoop;
	}

	public final String getState() {
		return state;
	}

}
