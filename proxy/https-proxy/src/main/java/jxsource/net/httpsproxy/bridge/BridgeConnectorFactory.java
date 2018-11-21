package jxsource.net.httpsproxy.bridge;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import jxsource.net.httpsproxy.proxy.Connector;
import jxsource.net.httpsproxy.proxy.ConnectorFactory;

public class BridgeConnectorFactory  extends ConnectorFactory {

	public Connector createConnector(){
		BridgeConnector connector = new BridgeConnector(); 
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
