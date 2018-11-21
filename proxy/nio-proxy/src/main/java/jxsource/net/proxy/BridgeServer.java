package jxsource.net.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class BridgeServer implements Runnable {

	protected Selector selector;
	private Logger logger = Logger.getLogger(BridgeServer.class);
	private ExecutorFactory executorFactory;
	private ServerSocketChannel serverSocketChannel;
	
	public void setExecutorFactory(ExecutorFactory executorFactory) {
		this.executorFactory = executorFactory;
	}
	public void init() throws IOException {
		serverSocketChannel = executorFactory
				.createServerSocketChannel();
		InetSocketAddress isa = new InetSocketAddress(executorFactory.getHost(),
				executorFactory.getPort());
		serverSocketChannel.socket().bind(isa);
		serverSocketChannel.configureBlocking(false);
		logger.info("listen on :" + serverSocketChannel);
		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

	}

	public void run() {
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
						logger.debug("accept: "
								+ localSocketChannel.getRemoteAddress());
//						localSocketChannel.configureBlocking(false);

						Executor executor = executorFactory.createExecutor(); 
						executor.setLocalSocketChannel(localSocketChannel);
						executor.init();
//						WorkThread worker = new WorkThread();
						ExecutorThread worker = new ExecutorThread();
						worker.setExecutor(executor);
						worker.start();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
