package jxsource.net.httpproxy.entity;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import jxsource.net.httpproxy.socket.SocketAccessor;

/*
 * implemented classes:
 * 	ProxyHttpEntityDestinationSocketManager
 * 	BufferedEntityDestinationSocketManager
 * 
 */
public interface EntityDestinationSocketManager {
	public EntityDestinationOutputStream getEntityDestinationSocket(
			HttpRequest request, // proxy client request used for determine cache file name
			HttpResponse response, // remote server response
			SocketAccessor controller);
}
