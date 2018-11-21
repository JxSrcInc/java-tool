package jxsource.net.proxy;

import java.io.IOException; 
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class PlainSocketChannelFactory implements SocketChannelFactory{

	public ServerSocketChannel createServerSocketChannet(String host, int port) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(host, port);
		serverSocketChannel.socket().bind(isa);
		return serverSocketChannel;
	}
	public SocketChannel createSocketChannel(String host, int port) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(host, port);
		socketChannel.connect(isa);
		return socketChannel;
	}
	public ServerSocketChannel createServerSocketChannet() throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		return serverSocketChannel;
	}
	public SocketChannel createSocketChannel() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		return socketChannel;
	}

}
