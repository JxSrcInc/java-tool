package jxsource.net.proxy;

import java.nio.channels.SocketChannel;

import jxsource.net.proxy.exception.LocalRequestException;
import jxsource.net.proxy.exception.RemoteConnectionException;
import jxsource.net.proxy.exception.RemoteResponseException;

/*
 * Used by Worker and Transaction 
 * Should be implemented by every transaction protocol
 */
public interface Executor {
	
	public static final String E_Wait = "E_Wait";
	public static final String E_LocalRequestRead = "E_LocalRequestRead";
	public static final String E_RemoteRequestWritten = "E_RemoteRequestWritten";
	public static final String E_RemoteResponseRead = "E_RemoteResponseRead";
	public static final String E_TransComplete = "E_TransComplete";
	// may be recoverable by re-connection
	public static final String E_RemoteChannelError = "E_RemoteChannelError";
	// unrecoverable error
	public static final String E_Error = "E_Error";
	
	// configuration
	public void setControllerManager(ControllerManager controllerManager);

	/*
	 * Each thread has its own Executor (and localChannel)
	 */
	public UrlInfo getConnectToUrlInfo();
	public SocketChannel getLocalChannel();
//	public SocketChannel getRemoteChannel();
	/*
	 * message from implementation for Worker and Transaction to display or use in log
	 */
	public String getMessage();
	public Exception getCatchedException();
	public String getState();
	/*
	 * set by Worker
	 */
	public void setLocalSocketChannel(SocketChannel localChannel);
	public void prepairReconnect();
	/*
	 * called by Worker to clean all remote connections 
	 * before thread stops
	 */ 
	public SocketChannel getRemoteChannel();
//	public ChannelController getRemoteChannelController();
	// Normally return E_LocalRequestRead
	public String procLocalRequest();
	// Normally return E_RemoteRequestWriten
	public String procRemoteRequest();
	// Normally return E_LocalRequestRead
	public String procRemoteResponse();
	// complete one transaction
	public void reset();
	// like post action of a bean
	public void init();
	public void destroy();
	
	public ControllerManager getControllerManager();

}
