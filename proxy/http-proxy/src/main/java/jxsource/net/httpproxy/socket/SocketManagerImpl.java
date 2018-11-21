package jxsource.net.httpproxy.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import jxsource.net.httpproxy.UrlInfo;
import jxsource.net.httpproxy.exception.RemoteServerError;

/*
 * This is a ThreadLocal object. -- No concurrent issue
 */
public class SocketManagerImpl implements SocketManager{

	private String socketFactory;
	private Map<UrlInfo, Socket> urlToSocketMap = new HashMap<UrlInfo, Socket>();
	private Map<Socket, UrlInfo> socketToUrlMap = new HashMap<Socket, UrlInfo>();
	private Socket currentSocket;
	
	public Socket getSocket(UrlInfo urlInfo) {
		if(urlToSocketMap.containsKey(urlInfo)) {
			currentSocket = urlToSocketMap.get(urlInfo);
		} else {
			currentSocket = createSocket(urlInfo);
			urlToSocketMap.put(urlInfo, currentSocket);
			socketToUrlMap.put(currentSocket, urlInfo);
		}
		return currentSocket;
	}

	private Socket createSocket(UrlInfo urlInfo) {
		try {
			Socket socket = null;
			if(socketFactory.equals("SSLSocket")) {
				socket = SSLSocketFactory.getDefault().createSocket();
			} else {
				socket = new Socket();
			}
			socket.setSendBufferSize(65536*2);
			String hostName = urlInfo.getHostName();
			InetSocketAddress isa = new InetSocketAddress(
					hostName, 
					urlInfo.getPort());
			socket.connect(isa);
			return socket;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteServerError("Fail to create socket for remote server.",e);
		}
			
			
	}
	
	/*
	 * returned Socket is closed.
	 */
	public Socket _removeSocket(Socket socket) {
		UrlInfo urlInfo = this.socketToUrlMap.remove(socket);
		if(urlInfo == null) {
			return null;
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.urlToSocketMap.remove(urlInfo);
	}
	public Socket removeSocket(Socket socket) {
		if(currentSocket != null && currentSocket.equals(socket)) {
			currentSocket = null;
		}
		return _removeSocket(socket);
	}

	/*
	 * This method releases the url of currentSocket but does not close it.
	 * It allows the url to bind to new socket but allows the currentSocket still 
	 * to download large video files in backend until it finishes or aborted
	 */
	public Socket removeSocket() {
		UrlInfo urlInfo = this.socketToUrlMap.remove(currentSocket);
		if(urlInfo == null) {
			return null;
		}
		currentSocket = null;
		return this.urlToSocketMap.remove(urlInfo);
	}

	public void releaseSocket(Socket socket) {
		
		
	}

	public void setSocketFactory(String socketFactory) {
		this.socketFactory = socketFactory;
	}

}
