package jxsource.net.proxy.delegate;

import java.net.Socket;

import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.http.RequestInfo;

public interface Processor {
	public RequestHandler getRequestHandler();
	public void handleRequest(RequestInfo remoteInfo);
}
