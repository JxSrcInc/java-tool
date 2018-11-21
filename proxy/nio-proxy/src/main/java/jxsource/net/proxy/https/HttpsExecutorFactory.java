package jxsource.net.proxy.https;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import jxsource.net.proxy.Executor;
import jxsource.net.proxy.ExecutorFactory;

public class HttpsExecutorFactory implements ExecutorFactory{

	public Executor createExecutor() {
		HttpsExecutor executor = new HttpsExecutor();
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
		return 10090;
	}

}
