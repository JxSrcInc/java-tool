package jxsource.net.httpsproxy.proxy;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import jxsource.net.httpproxy.trace.TransferWatcher;

/*
 * Standard SSL Proxy 
 * client make a http CONNECT call to proxy
 * proxy makes plain socket connection to remote server and inform client a successful connection  
 * client uses the connection to build SSL channel
 * 
 */
public class SSLProxyServer implements Runnable {

	protected Selector selector;
	private Logger logger = Logger.getLogger(SSLProxyServer.class);
	private ConnectorFactory connectorFactory;
	private ServerSocketChannel serverSocketChannel;
	
	public void setConnectorFactory(ConnectorFactory connectorFactory) {
		this.connectorFactory = connectorFactory;
	}
	public void init() throws IOException {
		serverSocketChannel = connectorFactory
				.createServerSocketChannel();
		InetSocketAddress isa = new InetSocketAddress(connectorFactory.getHost(),
				connectorFactory.getPort());
		serverSocketChannel.socket().bind(isa);
		serverSocketChannel.configureBlocking(false);
		logger.info("listen on :" + serverSocketChannel);
		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		try {
	        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
	        ObjectName name = new ObjectName("jxsource.net.httpproxy:type=TransactionWatcher"); 
	        TransferWatcher mbean = TransferWatcher.getInstance(); 
	        mbs.registerMBean(mbean, name); 
			} catch(Exception e) {
				throw new RuntimeException("Fail to create TransactionWatcher MBean.", e);
			}

	}

	public void run() {
//	    ExecutorService executor = Executors.newFixedThreadPool(5); 
		while (true) {
			try {
				selector.select();
				Iterator<SelectionKey> selectionKeys = selector.selectedKeys()
						.iterator();
				while (selectionKeys.hasNext()) {
					SelectionKey key = selectionKeys.next();
					selectionKeys.remove();
					if (key.isAcceptable()) {
						ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
								.channel();
						SocketChannel localSocketChannel = serverSocketChannel
								.accept();
						localSocketChannel.configureBlocking(false);
						logger.debug("accept: "
								+ localSocketChannel.getRemoteAddress());

						SelectorThread worker = new SelectorThread();
						worker.setLocalChannel(localSocketChannel);
						worker.setConnector(connectorFactory.createConnector());
//						worker.start();
//			            executor.execute(worker); 
						new Thread(worker).start();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
