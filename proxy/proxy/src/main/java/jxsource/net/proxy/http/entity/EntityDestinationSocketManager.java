package jxsource.net.proxy.http.entity;

import jxsource.net.proxy.Controller;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/*
 * implemented classes:
 * 	ProxyHttpEntityDestinationSocketManager
 * 	BufferedEntityDestinationSocketManager
 * 
 */
public interface EntityDestinationSocketManager {
	public EntityDestinationOutputStream getEntityDestinationSocket(
			HttpRequest request, HttpResponse response, Controller controller);
}
