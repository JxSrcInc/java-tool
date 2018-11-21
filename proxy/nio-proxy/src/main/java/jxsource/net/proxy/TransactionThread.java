package jxsource.net.proxy;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import jxsource.net.proxy.exception.ConnectStopException;
import jxsource.net.proxy.exception.LocalRequestException;
import jxsource.net.proxy.exception.RemoteConnectionException;
import jxsource.net.proxy.http.HttpExecutor;
import jxsource.net.proxy.http.exception.MessageHeaderException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

/*
 * BridgeServer must pass Local socket channel to Executor
 * and then pass the executor to WorkThread before starting work thread.
 */
public class TransactionThread extends Thread {
	public static final String Local = "Local";
	public static final String Remote = "Remote";
	private Utils utils = new Utils();
	private Logger logger = Logger.getLogger(TransactionThread.class);
	private Transaction executor;
	private int transactionCount = 0;
	private SocketChannel localChannel;

	// BridgeServer call this method to set Executor before start work thread
	public void setExecutor(Transaction executor) {
		this.executor = executor;
	}

	@Override
	public synchronized void start() {
			if (executor == null) {
				logger.error("executor is null.");
				return;
			} else {
				localChannel = executor.getLocalChannel();
			}
			super.start();
	}
	
	private String prefix() {
		return Thread.currentThread().getName()+" [WorkerThread] - ";
	}

	public void run() {
		boolean ready = true;
		while (ready) {
			try {
				logger.info("start transaction "
						+ (transactionCount++));
								String executorState = executor.procLocalRequest();
								logger.debug("Local Read: Executor = "+executorState);
								boolean transactionComplete = false;
								if(executorState.equals(Transaction.E_LocalRequestRead)) {
									for(int i=0; i<2; i++) {
										SocketChannel remoteChannel = executor.getRemoteChannel();
										
										if (!remoteChannel.isConnected()) {
											logger.debug("remote not connected");
											UrlInfo urlInfo = executor.getConnectToUrlInfo();
											InetSocketAddress isa = new InetSocketAddress(
												urlInfo.getHostName(),
												urlInfo.getPort());
											try {
												remoteChannel.connect(isa);
											} catch (ConnectException ce) {
												logger.debug("Error when connect to "+urlInfo, ce);
												executor.prepairReconnect();
												// reconnect
												continue;
											}
										}
										executorState = executor.procRemoteRequest();
										if(executorState.equals(Transaction.E_RemoteRequestWritten)) {
											executorState = executor.procRemoteResponse();
											if(executorState.equals(Transaction.E_TransComplete)) {
												executor.reset();
												// complete transaction, break for loop
												transactionComplete = true;
												break;
											}
										} 
										logger.error("Executor state = "+executorState+'\n'+
												executor.getMessage(), executor.getCatchedException());	
										// reconnect
										executor.prepairReconnect();
									} // end if loop
									if(!transactionComplete) {
										// TODO: send 400 message to client
										// and wait for next request
										logger.error("####### Cannot complet transaction after a reconnection.  client with 400. - stop thread instead.");
										ready = false;
									}
								} else 
								if(executorState.equals(Transaction.E_Error)) {
									// fail to process local request. 
									// TODO: send request error message to client and wait for next request.
									if(!(executor.getCatchedException() instanceof MessageHeaderException)) {
										logger.debug("local request error", executor.getCatchedException());
									}
									// or client close connection -> stop thread
									ready = false;
								}
				
			} catch (Exception e) {
				// Selector Exception
				logger.error("Selector error.", e);
				ready = false;
			}
		}
		stopThread();
	}

	private void stopThread() {
		executor.destroy();
		utils.closeSocketChannel(localChannel);
		executor = null;
		logger.info("Stop");
	}

}
