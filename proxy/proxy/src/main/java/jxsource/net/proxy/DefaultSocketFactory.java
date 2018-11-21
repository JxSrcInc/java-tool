package jxsource.net.proxy;

import java.io.IOException; 
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class DefaultSocketFactory implements SocketFactory{

	private ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
	private SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
	
	public ServerSocket createSSLServerSocket(String host, int port) throws IOException {
		ServerSocket serverSocket = ssf.createServerSocket();
		InetSocketAddress isa = new InetSocketAddress(host, port);
		serverSocket.bind(isa);
		return serverSocket;
	}
	public Socket createSSLSocket(String host, int port) throws IOException {
		Socket socket = sf.createSocket();
		InetSocketAddress isa = new InetSocketAddress(host, port);
		socket.connect(isa);
		return socket;
	}
	public ServerSocket createSSLServerSocket() throws IOException {
		return ssf.createServerSocket();
	}
	public Socket createSSLSocket() throws IOException{
		return sf.createSocket();
	}
	
	public ServerSocket createServerSocket(String host, int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket();
		InetSocketAddress isa = new InetSocketAddress(host, port);
		serverSocket.bind(isa);
		return serverSocket;
	}
	public Socket createSocket(String host, int port) throws IOException {
		Socket socket = new Socket();
		InetSocketAddress isa = new InetSocketAddress(host, port);
		socket.connect(isa);
		return socket;
	}
	public ServerSocket createServerSocket() throws IOException {
		return new ServerSocket();
	}
	public Socket createSocket() {
		return new Socket();
	}

}
