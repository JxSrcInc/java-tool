package jxsource.net.proxy.http;

import java.net.Socket;

import jxsource.net.proxy.delegate.Processor;
import jxsource.net.proxy.delegate.RequestHandler;
import jxsource.net.proxy.exception.ProcessorException;
import jxsource.net.proxy.exception.RemoteServerCloseException;

import org.apache.log4j.Logger;

public class HttpProcessor implements Processor{
	private static Logger logger = Logger.getLogger(HttpProcessor.class);
	private HttpRequestHandler requestHandler;
	
	public void setRequestHandler(HttpRequestHandler httpRequestHandler) {
		this.requestHandler = httpRequestHandler;
	}
	public RequestHandler getRequestHandler() {
		return requestHandler;
	}

	public void handleRequest(RequestInfo remoteInfo) {
		requestHandler.setRemoteInfo(remoteInfo);

		try{
			String executorState = requestHandler.procLocalRequest();
//			logger.debug("Local Read: Executor = "+executorState);
			boolean transactionComplete = false;
			if(executorState.equals(HttpRequestHandler.E_LocalRequestRead)) {
			// reconnect for loop
			for(int i=0; i<2; i++) { 
				Socket remoteSocket = requestHandler.getRemoteSocket();
				
				if (!remoteSocket.isConnected()) {
					if(!requestHandler.connectRemoteHost().equals(HttpRequestHandler.E_RemoteHostConnected)) {
						requestHandler.prepairReconnect();
						// reconnect
						continue;												
					}
				}
				executorState = requestHandler.procRemoteRequest();
				if(executorState.equals(HttpRequestHandler.E_RemoteRequestWritten)) {
					executorState = requestHandler.procRemoteResponse();
					if(executorState.equals(HttpRequestHandler.E_TransComplete)) {
						requestHandler.reset();
						// complete transaction, break for loop
						transactionComplete = true;
						// break for reconnection loop
						break;
					} else 
					if(executorState.equals(HttpRequestHandler.E_RemoteConnectionClosed)) {
						// remote connection is cleaned up by requestHandler
						// throw exception to close WorkThread.
						throw new RemoteServerCloseException("Remote Connection Closed.");
					} else 
					if(executorState.equals(HttpRequestHandler.E_Error)) {
						// part of data may be sent to client
						// proxy should not make reconnect
						// let client to decide
						logger.error("Error when processing remote response.\n"+requestHandler.getMessage(), requestHandler.getCatchedException());
						requestHandler.cleanRemoteSocket();
						// break for reconnection loop
						break;
					} else {
						// do reconnection
						// most case is error when processing response header
						// bacause remote socket connection time out
					}
				} 
				logger.error("Executor state = "+executorState+'\n'+
						requestHandler.getMessage(), requestHandler.getCatchedException());	
				logger.debug("try re-connect to "+requestHandler.getConnectToUrlInfo());
				// reconnect
				requestHandler.prepairReconnect();
			} // end reconnect for loop
			if(!transactionComplete) {
				// TODO: send 400 message to client
				// and wait for next request
				logger.error("####### Cannot complet transaction.  client with 400. - stop thread instead.\n"+requestHandler.getMessage()+"\n"+requestHandler.getCatchedException());
				throw new ProcessorException("Cannot complete transaction.");
			} // else - transaction complete -> continue
			} else {
				// Post method fails on reading message body from local socket 
				throw new ProcessorException("Error when reading on local socket.",requestHandler.getCatchedException());
			}

		} finally {
			requestHandler.reset();
		}
	}

}
