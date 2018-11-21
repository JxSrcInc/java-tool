package jxsource.net.httpproxy.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;

import jxsource.net.httpproxy.UrlInfo;

/*
 * Controller manages socket access
 *
 * All working thread must first get controller from controller manager
 * then it can access a socket from the controller
 *
 * Updated: Use setUseSSL() method to select Socket and SSLSocket. default is Plain Socket
 */
public interface SocketAccessor {
	/*
	 * set parameter useSSL to true to use SSLSocket
	 */
	public void setUseSSL(boolean useSSL);
	public void setUrlInfo(UrlInfo urlInfo);
	public UrlInfo getUrlInfo();
	public Socket getSocket() throws IOException;
		/*
	 * Calling class must set its controller member to null immediately after this call
	 * In other words, calling class should not use its controller member immediately after this call
	 *
	 * TODO: However, above requirements cannot be enforced in program in current design.
	 * It is programmer responsibility to implement it.
	 * A SocketWrap may used to replace Socket later to remove programmer's responsibility to implement
	 * the above requirement.
	 *
	 */
	public void releaseSocket();
	/*
	 * Calling class must set its controller member to null immediately after this call
	 * In other words, calling class should not use its controller member immediately after this call
	 *
	 * TODO: However, above requirements cannot be enforced in program in current design.
	 * It is programmer responsibility to implement it.
	 * A SocketWrap may used to replace Socket later to remove programmer's responsibility to implement
	 * the above requirement.
	 *
	 * After this call, notified thread will reset the socket in lockSocket() call
	 * See lockSocket code for details.
	 * However the element with key of this.urlInfo is still available in ControllerManager
	 *
	 * TODO: Should remove this.urlInfo from ControllerManager?
	 */
//	public void closeSocket();
		/*
	 * Create a new socket connection using the same URL
	 */
	public Socket reconnectSocket() throws IOException;
	public Socket removeSocket();
}
