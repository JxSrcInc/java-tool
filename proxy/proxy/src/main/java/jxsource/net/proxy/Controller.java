package jxsource.net.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.log4j.Logger;

/*
 * Controller manages socket access
 * 
 * All working thread must first get controller from controller manager
 * then it can access a socket from the controller
 * 
 * Updated: Use setUseSSL() method to select Socket and SSLSocket. default is Plain Socket
 */
public class Controller {
	private static Logger logger = Logger.getLogger(Controller.class);
	private volatile SocketFactory socketFactory;
	private volatile Socket socket; // remote socket
	private final String hostname;
	private final int port;
	private volatile boolean lock;
	private boolean useSSL = false;

	public Controller(SocketFactory socketFactory, String hostname, int port) {
		this.socketFactory = socketFactory;
		this.hostname = hostname;
		this.port = port;
	}

	/*
	 * set parameter useSSL to true to use SSLSocket
	 */
	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}
	private Socket _createSocket() {
		if(useSSL) {
			try {
				return socketFactory.createSSLSocket();
			} catch (IOException e) {
				throw new RuntimeException("Cannot create SSL socket.",e);
			}
		} else {
			return socketFactory.createSocket();
		}
	}
	private Socket _createSocket(String hostname, int port) throws IOException {
		if(useSSL) {
			return socketFactory.createSSLSocket(hostname, port);
		} else {
			return socketFactory.createSocket(hostname, port);
		}
	}

	public Socket getSocket() {
		synchronized (this) {
//			logger.debug("getSocket: "+toString());
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
//			logger.debug("locked: "+toString());
			if (socket == null) {
				socket = _createSocket();
//				logger.debug("Controller " + hashCode() + ": create socket "
//						+ socket.hashCode());
			}
			return socket;
		}
	}

	public Socket getSocket(String hostname, int port) throws IOException {
		synchronized (this) {
//			logger.debug("getSocket: "+toString());
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
//			logger.debug("locked: "+toString());
			if (socket == null) {
				socket = _createSocket(hostname, port);//socketFactory.createSocket(hostname, port);
//				logger.debug("Controller " + hashCode() + ": create socket "
//						+ socket.hashCode());
			}
			return socket;
		}
	}

	public Socket getSocket(UrlInfo urlInfo) throws IOException {
		String hostname = urlInfo.getHostname();
		int port = urlInfo.getPort();
		synchronized (this) {
//			logger.debug("getSocket: "+toString());
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
//			logger.debug("locked: "+toString());
			if (socket == null) {
				socket = _createSocket(hostname, port);
//				logger.debug("Controller " + hashCode() + ": create socket "
//						+ socket.hashCode());
			}
			return socket;
		}
	}

	public void releaseSocket(Socket socket) {
		synchronized (this) {
			if(this.socket != null && socket != null ) {
				SocketAddress addr = socket.getRemoteSocketAddress();
				SocketAddress thisAddr = this.socket.getRemoteSocketAddress();
				if(addr != null && thisAddr != null && !addr.toString().equals(thisAddr.toString())) {
					// the parameter socket is not associated with THIS controller
					// One scenario for this case:
					// it is removed from this controller before 
					// and still used by application 
					// now the application completes its work and releases the socket
					// Because the parameter socket does not associate to any controller,
					// it needs to close to release resource. -- or do nothing
//					logger.info("release a removed socket "+socket);
					try {
						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				System.err.println(Thread.currentThread().getName()+" Controller.releaseSocket(): either this.socket or parameter socket is null ");
			}
//			logger.debug("release: "+toString());				
			lock = false;
			notifyAll();
		}
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
	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public boolean isLock() {
		return lock;
	}

	@Override
	public String toString() {
		return "Controller("+hashCode()+") [hostname=" + hostname + ", port=" + port
				+ ", lock=" + lock + ", socket=" + socket + "]";
	}


}
