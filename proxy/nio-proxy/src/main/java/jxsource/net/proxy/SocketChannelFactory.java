package jxsource.net.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public interface SocketChannelFactory { 

	public ServerSocketChannel createServerSocketChannet(String host, int port) throws IOException;
	public SocketChannel createSocketChannel(String host, int port) throws IOException;
	public ServerSocketChannel createServerSocketChannet() throws IOException;
	public SocketChannel createSocketChannel() throws IOException;

}
