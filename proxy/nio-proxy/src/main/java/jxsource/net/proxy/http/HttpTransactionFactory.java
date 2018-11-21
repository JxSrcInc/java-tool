package jxsource.net.proxy.http;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import jxsource.net.proxy.ChannelManagerImpl;
import jxsource.net.proxy.Transaction;
import jxsource.net.proxy.TransactionFactory;
import jxsource.net.proxy.PlainSocketChannelFactory;
import jxsource.net.proxy.http.entity.EntityDestinationChannelManagerImpl;

public class HttpTransactionFactory implements TransactionFactory{

	public Transaction createExecutor() {
//		HttpExecutor executor = new HttpExecutor();
		HttpTransaction executor = new HttpTransaction();
		executor.setChannelManager(
				new ChannelManagerImpl<PlainSocketChannelFactory>(
						new PlainSocketChannelFactory()));
		executor.setEntityDestinationChannelManager(new EntityDestinationChannelManagerImpl());
		return executor;
	}

	public ServerSocketChannel createServerSocketChannet() throws IOException
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
