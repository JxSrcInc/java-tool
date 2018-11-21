package jxsource.net.proxy.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import jxsource.net.proxy.Controller;
import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.SocketUtils;
import jxsource.net.proxy.UrlInfo;
import jxsource.net.proxy.delegate.RequestHandler;
import jxsource.net.proxy.http.entity.EntityDestinationOutputStream;
import jxsource.net.proxy.http.entity.EntityDestinationSocketManager;
import jxsource.net.proxy.http.entity.EntityProcessor;
import jxsource.net.proxy.http.entity.EntityProcessorImpl;
import jxsource.net.proxy.http.entity.EntityStatus;
import jxsource.net.proxy.http.exception.MessageHeaderException;
import jxsource.net.proxy.http.upload.UploadDestinationOutputStream;
import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.string.StringUtils;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

public class HttpRequestHandler implements RequestHandler {
	public static final String E_Wait = "E_Wait";
	public static final String E_LocalRequestRead = "E_LocalRequestRead";
	public static final String E_RemoteHostConnected = "E_RemoteHostConnected";
	public static final String E_RemoteRequestWritten = "E_RemoteRequestWritten";
	public static final String E_RemoteResponseRead = "E_RemoteResponseRead";
	public static final String E_TransComplete = "E_TransComplete";
	public static final String E_RemoteConnectionClosed = "E_RemoteConnectionClosed";
	// may be recoverable by re-connection
	public static final String E_RemoteConnectionError = "E_RemoteConnectionError";
	// unrecoverable error
	public static final String E_Error = "E_Error";

	protected Logger logger = Logger.getLogger(HttpRequestHandler.class);

	protected SocketUtils socketUtils = new SocketUtils();
	protected HttpUtils httpUtils = new HttpUtils();
	protected HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	protected HttpRequest request;
	protected HttpResponse response;
	protected Socket localSocket;
	protected Socket remoteSocket;
	protected InputStream localInputStream;
	protected OutputStream localOutputStream;
	protected InputStream remoteInputStream;
	protected OutputStream remoteOutputStream;
	protected RequestInfo requestInfo;
	protected UrlInfo urlInfo;
	protected EntityProcessor entityProcessor = new EntityProcessorImpl();
	protected Exception catchedException;
	protected volatile ControllerManager controllerManager;
	protected volatile Controller controller;
	protected EntityDestinationSocketManager entityDestinationSocketManager;
	protected byte[] requestBody;
	protected String state = E_Wait;
	
	// Configuration
	public void setEntityDestinationSocketManager(EntityDestinationSocketManager entityDestinationSocketManager) {
		this.entityDestinationSocketManager = entityDestinationSocketManager;
	}
	public void setControllerManager(ControllerManager controllerManager) {
		this.controllerManager = controllerManager;
	}
	public void setRemoteInfo(RequestInfo remoteInfo) {
		this.requestInfo = remoteInfo;
		urlInfo = remoteInfo.getUrlInfo();
		request = remoteInfo.getRequest();
	}
	public void init() {} // not use
	public String getState() {
		return state;
	}
	public ControllerManager getControllerManager() {
		return controllerManager;
	}
	public Exception getCatchedException() {
		return catchedException;
	}
	public Controller getController() {
		return controller;
	}
	public UrlInfo getConnectToUrlInfo() {
		return urlInfo;
	}

	public void reset() {
		// when destroy calls reset(), controller is set to null when transaction completes
		if(controller != null) {
			controller.releaseSocket(remoteSocket);
		}
		clean();
	}
	
	private void clean() {
		request = null;
		response = null;
		requestBody = null;
		requestInfo = null;
		// controller and remoteSocket must be released but not destroyed
		remoteSocket = null;
		remoteInputStream = null;
		remoteOutputStream = null;
		controller = null;
		state = E_Wait;
		catchedException = null;
		
	}

	public void prepairReconnect() {
		controller.removeSocket();;
		state = E_LocalRequestRead;
		remoteSocket = null;
		remoteInputStream = null;
		remoteOutputStream = null;
		controller = null;
	}
	public void cleanRemoteSocket() {
		logger.info("clean remote socket");
		if(controller != null) {
			controller.removeSocket();
		}
		clean();
	}

	public String getMessage() {
		String msg = "Executor: "+state+"\n" + request;
		if(request.getRequestLine().getMethod().toUpperCase().equals("POST")) {
			msg += "\n"+new String(requestBody);
		}
		msg += "\n" + response;
		return StringUtils.addTabToLine(msg);
	}

	// called by BridgeServer
	public void setLocalSocket(Socket localSocket) throws IOException {
		this.localSocket = localSocket;
		localInputStream = localSocket.getInputStream();
		localOutputStream = localSocket.getOutputStream();
	}

	// called by WorkThread
	public Socket getLocalSocket() {
		return this.localSocket;
	}

	public String procLocalRequest() {
		requestBody = null;
		try {
			headerUtils.setHttpMessage(request);
			String method = request.getRequestLine().getMethod().toUpperCase();
			if(method.equals("GET")) {
				// continue
			} else 
			if(method.equals("POST")) {
				UploadDestinationOutputStream ddos = new UploadDestinationOutputStream(request);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ddos.setOutputStream(out);
				EntityProcessor entityProcessor = new EntityProcessorImpl();
				EntityStatus status = entityProcessor.processEntity(request, localInputStream, ddos);
				if(status.getStatus().equals(EntityProcessor.EntityComplete)) {
					requestBody = out.toByteArray();
				} else {
					System.err.println("UploadDestinationOutputStream error: "+status);
					System.exit(1);					
				}
				logger.debug("POST message: \n\t"+new String(requestBody));
			} else {
				System.err.println("Un-handled method: "+method);
			}

			return state = E_LocalRequestRead;
		} catch(Exception e) {
			catchedException = e;
			return state = E_Error;
		}
	}
	/*
	 * returns a not-connected remote socket
	 */
	public Socket getRemoteSocket() {

			controller = controllerManager.getController(urlInfo);
			remoteSocket = controller.getSocket();
			// NOTE: Don't save remoteInputStream and remoteOutputStream pointers at this time
			// because remoteSocket is not connected so not sure if it is safe to do it now.
			// Do them later in procRemoteRequest() nd procRemoteResponse()
			return remoteSocket;

	}

	public String connectRemoteHost() {
		UrlInfo urlInfo = getConnectToUrlInfo();
		// TODO: show hashCode
		logger.debug("connect to "+urlInfo.hashCode()+","+urlInfo);
		InetSocketAddress isa = new InetSocketAddress(
			urlInfo.getHostName(),
			urlInfo.getPort());
		try {
			remoteSocket.connect(isa);
			return state = E_RemoteHostConnected;
		} catch (Exception ce) {
			catchedException = ce;
			logger.debug("Error when connect to "+urlInfo, ce);
			return state = E_RemoteConnectionError;
		}
	}

	public String procRemoteRequest() {
		try {
			String debugMsg = "" + request;
			if(request.getRequestLine().getMethod().toUpperCase().equals("POST")) {
				debugMsg += "\n"+new String(requestBody);
			}
			logger.debug(debugMsg);
			// set up remote IO
			remoteOutputStream = remoteSocket.getOutputStream();
			httpUtils.outputRequest(request, remoteOutputStream);
			if(requestBody != null) {
				remoteOutputStream.write(requestBody);
				remoteOutputStream.flush();
			}
			return state = E_RemoteRequestWritten;
		} catch (Exception e) {
			// TODO: reconnect?
			catchedException = e;
			return state = E_RemoteConnectionError;
		}
	}

	// This method write response header then response entity if response has an entity
	public String procRemoteResponse() {
		try {
			remoteInputStream = remoteSocket.getInputStream();
			// Read response message head from target socket 
			ByteArray head = httpUtils.getMessageHead(remoteInputStream);
			state = E_RemoteResponseRead;
			response = httpUtils.getHttpResponse(head);
			logger.debug("" + response);
			// write response message head to src socket
			socketUtils.writeByteArrayToChannel(head.getArray(), localSocket);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 200 && statusCode != 302 && statusCode != 304 && statusCode != 204) {

				// process response message body
				EntityDestinationOutputStream edc = entityDestinationSocketManager.getEntityDestinationSocket(request, response, controller);
				edc.setOutputStream(localOutputStream);
				EntityStatus entityStatus = entityProcessor.processEntity(
						response, remoteInputStream, edc);
				logger.debug("" + entityStatus);
				if(entityStatus.getStatus().equals(EntityProcessor.EntityClosed) ||
						headerUtils.hasHeaderWithValue("Connection", "close")	) {
					this.cleanRemoteSocket();
					// Let WorkThread to close
					return E_RemoteConnectionClosed;
				}
			}
			// NOTE: Don't do reset() here.
			// TransactionThread will do it.
			return state = E_TransComplete;
		} catch (MessageHeaderException e) {
			// fail to process response header
			// Let TransactionThread decides weather or not to do a reconnection
			catchedException = e;
			return state = E_RemoteConnectionError;

		} catch (Exception e) {
			catchedException = e;
			if(state == E_RemoteResponseRead) {
				// may remote read error or local write error
				// but part data may send client so 
				return state = E_Error;
			} else {
				// duplcate catch as MessageHeaderException
				// Let TransactionThread decides weather or not to do a reconnection
				// TODO: remove one
				return state = E_RemoteConnectionError;
			}
		}

	}

}
