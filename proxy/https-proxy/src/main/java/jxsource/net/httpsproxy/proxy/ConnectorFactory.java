package jxsource.net.httpsproxy.proxy;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public class ConnectorFactory {

	public Connector createConnector() {
		Connector connector = new Connector(); 
		return connector;
	}

	public ServerSocketChannel createServerSocketChannel() throws IOException
	{
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		return serverSocketChannel;
	};

	public String getHost() {
		// TODO Auto-generated method stub
		return "localhost";
	}

	public int getPort() {
		return 9090;
	}

}
