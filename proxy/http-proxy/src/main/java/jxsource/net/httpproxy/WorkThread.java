package jxsource.net.httpproxy;

import java.net.Socket;
import org.apache.log4j.Logger;

import jxsource.net.httpproxy.exception.ProxyClientError;
import jxsource.net.httpproxy.exception.TransactionException;
import jxsource.net.httpproxy.socket.SocketManagerHolder;
import jxsource.net.httpproxy.trace.TransferWatcher;

public class WorkThread implements Runnable{
	
	private Logger logger = Logger.getLogger(WorkThread.class);
	private Socket localSocket;
	private int transactionCount = 0;
	private static int startedThread;
	private static int stopedThread;
	
	public WorkThread(Socket localSocket) {
		this.localSocket = localSocket;
		startedThread++;
	}

	public void run() {
		ProxyTransferProcessor localHandler = Config.getInstance().createProxyClientHandler();
		while (true) {
			try {
//				logger.info("start transaction "+ (transactionCount++));
				// Create RequestResponseProcessor every time to make simple and clean process.
				// pass execution to requestResponseProcessor.
				localHandler.proc(localSocket);
				localHandler.reset();
			} catch (ProxyClientError e) {
				logger.debug("client close connection.");
				break;				
			} catch (TransactionException e) {
//				logger.info("client close connection.");
				break;
			} catch (Throwable e) {
				// Catch any error and then stop thread.
				logger.error("WorkThread error.", e);
				break;
			}
		}
		stopThread();
	}

	private void stopThread() {
		// TODO: clean all remote connections
		// This is not efficient. Should improve using Controller.
		stopedThread++;
		try {
			localSocket.close();
			localSocket = null;
		} catch (Exception e) {
			logger.error("Error when close local socket", e);
		}
		logger.debug(Thread.currentThread().getName()+" Stop. startedThread="+startedThread+", runningThread="+(startedThread-stopedThread));
	}

	
}
