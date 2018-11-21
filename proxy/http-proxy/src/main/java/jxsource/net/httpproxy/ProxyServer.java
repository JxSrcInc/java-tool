package jxsource.net.httpproxy;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import jxsource.net.httpproxy.trace.TransferWatcher;

public class ProxyServer {

	private static String defaultConfigFile = "server.config";
	private Logger logger = Logger.getLogger(ProxyServer.class);
	private ServerSocket serverSocket;
	
	public void start(String configFile) {
		Config conf = Config.getInstance();
		try {
			serverSocket = conf.createServerSocket();
			logger.info("listen on :" + serverSocket);
		} catch(Exception e) {
			throw new RuntimeException("Fail to create ServerSocket.",e);
		}
		try {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
        ObjectName name = new ObjectName("jxsource.net.httpproxy:type=TransactionWatcher"); 
        TransferWatcher mbean = TransferWatcher.getInstance(); 
        mbs.registerMBean(mbean, name); 
		} catch(Exception e) {
			throw new RuntimeException("Fail to create TransactionWatcher MBean.", e);
		}
	       ExecutorService executor = Executors.newFixedThreadPool(5); 
		while(true) {
			try {
				Socket socket = serverSocket.accept();
//				logger.info("accept connection on "+socket);
//				socket.setSoTimeout(1000*3);
				WorkThread worker = new WorkThread(socket);
//				new Thread(worker).start();
	            executor.execute(worker); 
			} catch (IOException e) {
				logger.error("Cannot start work thread.", e);
			}
		}
	}
	
	public static void main(String[] args) {
		ProxyServer server = new ProxyServer();
		if(args.length > 0) {
			server.start(args[0]);
		} else {
			server.start(defaultConfigFile);
		}
	}
}
