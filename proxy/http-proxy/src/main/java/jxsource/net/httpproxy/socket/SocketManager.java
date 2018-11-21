package jxsource.net.httpproxy.socket;

import java.net.Socket;

import javax.net.SocketFactory;

import jxsource.net.httpproxy.UrlInfo;

public interface SocketManager {
	public Socket getSocket(UrlInfo urlInfo);
	public Socket removeSocket(Socket socket);
	public Socket removeSocket();
	public void releaseSocket(Socket socket);
	public void setSocketFactory(String socketFactory);
}
