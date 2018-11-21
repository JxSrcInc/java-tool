package jxsource.net.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

public class Controller {
	private static Logger logger = Logger.getLogger(Controller.class);
	private volatile SocketChannelFactory channelFactory;
	private volatile SocketChannel channel; // remote channel
	private final String hostname;
	private final int port;
	private volatile boolean lock;

	public Controller(SocketChannelFactory channelFactory, String hostname, int port) {
		this.channelFactory = channelFactory;
		this.hostname = hostname;
		this.port = port;
	}

	public SocketChannel getSocketChannel() throws IOException {
		synchronized (this) {
			logger.debug("getSocketChannel: "+toString());
			while (lock) {
				logger.debug("wait: "+toString());
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			lock = true;
			logger.debug("locked: "+toString());
			if (channel == null) {
				channel = channelFactory.createSocketChannel();
				logger.debug("Controller " + hashCode() + ": create channel "
						+ channel.hashCode());
			}
			return channel;
		}
	}
/*
	public void releaseSocketChannel(SocketChannel channel) {
		synchronized (this) {
			if(this.channel != null && channel != null ) {
				SocketAddress addr = channel.getRemoteSocketAddress();
				SocketAddress thisAddr = this.channel.getRemoteSocketAddress();
				if(addr != null && thisAddr != null && !addr.toString().equals(thisAddr.toString())) {
					// the parameter channel is not associated with THIS controller
					// One scenario for this case:
					// it is removed from this controller before 
					// and still used by application 
					// now the application completes its work and releases the channel
					// Because the parameter channel does not associate to any controller,
					// it needs to close to release resource. -- or do nothing
					logger.info("release a removed channel "+channel);
					try {
						channel.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				System.err.println(Thread.currentThread().getName()+" Controller.releaseSocketChannel(): either this.channel or parameter channel is null ");
			}
			logger.debug("release: "+toString());				
			lock = false;
			notifyAll();
		}
	}
	// Modified to reture the removed socked but not close it
	// so that the calling thread may still use it 
	// and release this controller for other threads to access the remote host when calling getSocketChannel()
	public SocketChannel removeSocketChannel() {
		synchronized (this) {
			SocketChannel removedSocketChannel = channel;
			if (channel != null) {
				logger.debug("remove: "+toString());
				channel = null;
			} else {
				System.err.println(Thread.currentThread().getName()+" Controller.removeSocketChannel(): channel cannot be null ");
			}
			lock = false;
			notifyAll();
			return removedSocketChannel;
		}
	}
*/
	public void releaseSocketChannel() {
		synchronized (this) {
		if(this.channel != null) {
			logger.debug("release: "+toString());
		} else {
			System.err.println(Thread.currentThread().getName()+" Controller.releaseSocketChannel(): this.channel cannot be null ");
		}
			lock = false;
			notifyAll();
		}
	}

	public void removeSocketChannel() {
		synchronized (this) {
			if (channel != null) {
				logger.debug("remove: "+toString());
				try {
					channel.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				channel = null;
			} else {
				System.err.println(Thread.currentThread().getName()+" Controller.removeSocketChannel(): channel cannot be null ");
			}
			lock = false;
			notifyAll();

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
				+ ", lock=" + lock + ", channel=" + channel + "]";
	}


}
