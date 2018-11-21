package jxsource.net.proxy.delegate;

import java.io.IOException;
import java.net.Socket;

import jxsource.net.proxy.ControllerManager;

/*
 * Used by Worker and Transaction 
 * Should be implemented by every transaction protocol
 */
public interface RequestHandler {
	// configuration
	public void setControllerManager(ControllerManager controllerManager);
	public void setLocalSocket(Socket localSocket) throws IOException;

}
