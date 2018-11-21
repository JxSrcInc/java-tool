package jxsource.net.proxy.delegate;

import java.net.Socket;

import javax.net.ssl.SSLSocket;

import jxsource.net.proxy.UrlInfo;
import jxsource.net.proxy.exception.ClientCloseException;
import jxsource.net.proxy.exception.RemoteServerCloseException;
import jxsource.net.proxy.http.HttpUtils;
import jxsource.net.proxy.http.RequestInfo;
import jxsource.net.proxy.http.exception.MessageHeaderException;

import org.apache.log4j.Logger;

public class WorkThread extends Thread{

	private Logger logger = Logger.getLogger(WorkThread.class);
	private int transactionCount = 0;
	private Delegate delegate;
	private volatile Socket localSocket;
	private HttpUtils httpUtils = new HttpUtils();
	private Processor processor;
	
	public WorkThread(Delegate delegate, Socket localSocket) {//, ControllerManager controllerManager) {
		this.delegate = delegate;
		this.localSocket = localSocket;
	}

	public void run() {
		boolean ready = true;
		while (ready) {
			try {
				logger.info("start transaction "+ (transactionCount++));
				
				RequestInfo remoteInfo = null;
				try {
					remoteInfo = httpUtils.createRequestInfo(localSocket.getInputStream());
				} catch(MessageHeaderException e) {
					throw new ClientCloseException(e);
				}
				// Add extract check to setup UrlInfo.protocol
				UrlInfo urlInfo = remoteInfo.getUrlInfo();
				if(urlInfo.getProtocol() == null) {
					if(localSocket instanceof SSLSocket) {
						urlInfo.setProtocol("https");
					} else {
						urlInfo.setProtocol("http");
					}
				}
				logger.debug("Request: "+remoteInfo.getRequest());
//				logger.debug("UrlInfo: "+remoteInfo.getUrlInfo());
				processor = delegate.getProcessor(remoteInfo.getUrlInfo());
				// each Processer has its RequestHandler 
				// but RequestHandler will share a common ControllerManager
				// which may be a global instance or thread based instance.
				processor.getRequestHandler().setLocalSocket(localSocket);
				processor.handleRequest(remoteInfo);
			} catch (ClientCloseException e) {
				ready = false;
			} catch (RemoteServerCloseException e) {
				ready = false;
			} catch (Exception e) {
				// Selector Exception
				logger.error("while loop error.", e);
				ready = false;
			}
		}
		stopThread();
	}

	private void stopThread() {
		try {
			localSocket.close();
		} catch (Exception e) {
			logger.error("Error when close local socket", e);
		}
		processor = null;
		logger.info("Stop");
	}

}
