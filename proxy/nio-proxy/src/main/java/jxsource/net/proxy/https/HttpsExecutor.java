package jxsource.net.proxy.https;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.Executor;
import jxsource.net.proxy.UrlInfo;

public class HttpsExecutor implements Executor{

	private static Logger logger = Logger.getLogger(HttpsExecutor.class);
	private SocketChannel localChannel;
	
	public void setControllerManager(ControllerManager controllerManager) {
		// TODO Auto-generated method stub
		
	}

	public UrlInfo getConnectToUrlInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public SocketChannel getLocalChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Exception getCatchedException() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLocalSocketChannel(SocketChannel localChannel) {
		try {
			localChannel.configureBlocking(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.localChannel = localChannel;
		
	}

	public void prepairReconnect() {
		// TODO Auto-generated method stub
		
	}

	public SocketChannel getRemoteChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String procLocalRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	public String procRemoteRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	public String procRemoteResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public ControllerManager getControllerManager() {
		// TODO Auto-generated method stub
		return null;
	}

}
