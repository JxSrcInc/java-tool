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
public class SingleHostSocketAccessor implements SocketAccessor{
	private static Logger logger = Logger.getLogger(SingleHostSocketAccessor.class);
//	private volatile SocketFactory socketFactory;
	private volatile Socket socket; // remote socket
//	private final String hostname;
//	private final int port;
	private volatile UrlInfo urlInfo;
	private volatile boolean lock;
	private volatile boolean useSSL = false;
	private volatile long lockedThreadId;
	private volatile String lockedThreadName;

	public void setUrlInfo(UrlInfo urlInfo) {
		this.urlInfo = urlInfo;
	}
	
	public UrlInfo getUrlInfo() {
		return urlInfo;
	}

	/*
	 * set parameter useSSL to true to use SSLSocket
	 */
	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	private Socket createSocket() throws IOException {
		Socket socket = null;
		if(useSSL) {
			socket = SSLSocketFactory.getDefault()
					.createSocket(urlInfo.getHostname(), urlInfo.getPort());
		} else {
			socket = new Socket(urlInfo.getHostname(), urlInfo.getPort());
		}
//		socket.setSoTimeout(1000*5);
//		socket.setReceiveBufferSize(1024*16);
		return new SocketAdapter(socket);
//		return socket;
	}

	public Socket getSocket() throws IOException{
		synchronized (this) {
//			logger.debug("lockSocket: "+toString());
			while (lock) {
//				logger.debug("wait: "+toString());
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			lock = true;
			lockedThreadId = Thread.currentThread().getId();
			lockedThreadName = Thread.currentThread().getName();
//			logger.debug("locked: "+toString());
			if (socket == null || socket.isClosed()) {
				socket = createSocket();
//				logger.debug("Controller " + hashCode() + ": create socket "
//						+ socket.hashCode());
			}
			return socket;
		}
	}
	/*
	 * Calling class must set its controller member to null immediately after this call
	 * In other words, calling class should not use its controller member immediately after this call
	 * 
	 * Note: released socket may be closed !!!!
	 * 
	 * TODO: However, above requirements cannot be enforced in program in current design. 
	 * It is programmer responsibility to implement it.
	 * A SocketWrap may used to replace Socket later to remove programmer's responsibility to implement 
	 * the above requirement.
	 * 
	 */
	public void releaseSocket() {
		synchronized (this) {
			if(Thread.currentThread().getId() !=  lockedThreadId) {
				invalidThreadCall("releaseSocket", Thread.currentThread().getName());
			}
				//			logger.debug("unlockSocket: "+toString());				
				lock = false;
				lockedThreadId = 0l;
				lockedThreadName = "";
				notifyAll();
		}
	}
	
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
/*	public void closeSocket() {
		synchronized (this) {
			if(Thread.currentThread().getId() !=  lockedThreadId) {
				invalidThreadCall("closeSocket", Thread.currentThread().getName());
			}
			// change to close anyway no matter locked thread Id equals calling thread Id.
				_closeSocket();				
				lock = false;
				lockedThreadId = 0l;
				notifyAll();
		}
	}
*/
	private void _closeSocket() {
		if(socket != null) {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}			
		socket = null;
		}
	}
	/*
	 * Create a new socket connection using the same URL
	 */
	public Socket reconnectSocket() throws IOException{
		synchronized (this) {
			if(Thread.currentThread().getId() ==  lockedThreadId) {
				_closeSocket();
				socket = createSocket();
				return socket;
			} else {
				// this should not happen
				String msg = invalidThreadCall("reconnectSocket", Thread.currentThread().getName());
				throw new IOException(msg);
			}
		}
	}

	private String invalidThreadCall(String method, String callingThread){
		String msg = method+": Calling thread is different from locked thread: \n\t" +
				"Calling Thread: "+ callingThread + "\n\t" +
				"Locked Thread:  "+ lockedThreadName + "\n\t" +
				"UrlInfo: "+urlInfo;
		System.err.println(msg);
		return msg;
	}
	// Modified to reture the removed socked but not close it
	// so that the calling thread may still use it 
	// and release this controller for other threads to access the remote host when calling getSocket()
	public Socket removeSocket() {
		synchronized (this) {
			Socket removedSocket = socket;
			if (socket != null) {
//				logger.debug("remove: "+toString());
				socket = null;
			} else {
				System.err.println(Thread.currentThread().getName()+" Controller.removeSocket(): socket cannot be null ");
			}
			lock = false;
			notifyAll();
			return removedSocket;
		}
	}
	@Override
	public String toString() {
		return "Controller("+hashCode()+") [hostname=" + urlInfo.getHostName() + ", port=" + urlInfo.getPort()
				+ ", lock=" + lock + ", socket=" + socket + "]";
	}


}
