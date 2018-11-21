package jxsource.net.proxy.http;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import jxsource.net.proxy.ChannelManagerImpl;
import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.ControllerManagerImpl;
import jxsource.net.proxy.Executor;
import jxsource.net.proxy.ExecutorFactory;
import jxsource.net.proxy.PlainSocketChannelFactory;
import jxsource.net.proxy.http.entity.EntityDestinationChannelManagerImpl;

public class HttpExecutorFactory implements ExecutorFactory{

	private ControllerManager cm = new ControllerManagerImpl<PlainSocketChannelFactory>(
			new PlainSocketChannelFactory());
	public Executor createExecutor() {
//		HttpExecutor executor = new HttpExecutor();
		HttpExecutor executor = new HttpExecutor();
//		executor.setControllerManager(
//				new ControllerManagerImpl<PlainSocketChannelFactory>(
//						new PlainSocketChannelFactory()));
		executor.setControllerManager(cm);
		executor.setEntityDestinationChannelManager(new EntityDestinationChannelManagerImpl());
		return executor;
	}

	public ServerSocketChannel createServerSocketChannel() throws IOException
	{
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		return serverSocketChannel;
	};

	public final String getHost() {
		// TODO Auto-generated method stub
		return "localhost";
	}

	public final int getPort() {
		return 10080;
	}

}
