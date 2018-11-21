package jxsource.net.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public interface SocketFactory { 

	public ServerSocket createSSLServerSocket(String host, int port) throws IOException;
	public Socket createSSLSocket(String host, int port) throws IOException;
	public ServerSocket createSSLServerSocket() throws IOException;
	public Socket createSSLSocket() throws IOException;
	public ServerSocket createServerSocket(String host, int port) throws IOException;
	public Socket createSocket(String host, int port) throws IOException;
	public ServerSocket createServerSocket() throws IOException;
	public Socket createSocket();
}
