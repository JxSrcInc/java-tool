package jxsource.net.proxy.delegate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class ProxyServer implements Runnable {

	private Logger logger = Logger.getLogger(ProxyServer.class);
	private Context context;
	private ServerSocket serverSocket;
	
	public void setContext(Context context) {
		this.context = context;
	}

	public void run() {
		try {
			String listenHost = context.getHost();
			int listenPort = context.getPort(); 
			if(listenHost == null) {
				serverSocket = new ServerSocket(listenPort);
			} else {
				serverSocket = context.getServerSocket();
				InetSocketAddress isa = new InetSocketAddress(listenHost, listenPort);
				serverSocket.bind(isa);
			}
			logger.info("listen on :" + serverSocket);
		} catch(Exception e) {
			throw new RuntimeException("Fail to create ServerSocket.",e);
		}
		
		while(true) {
			try {
				Socket localSocket = serverSocket.accept();
				WorkThread worker = new WorkThread(
						context.getDelegate(),
						localSocket
						);
				worker.start();
			} catch (IOException e) {
				logger.error("Cannot start work thread.", e);
			}
		}
	}
	
}
