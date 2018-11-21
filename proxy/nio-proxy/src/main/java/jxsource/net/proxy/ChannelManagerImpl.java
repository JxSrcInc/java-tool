package jxsource.net.proxy;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jxsource.net.proxy.http.exception.ConnectionManagerException;

import org.apache.log4j.Logger;

public class ChannelManagerImpl<T extends SocketChannelFactory> implements ChannelManager{
	private static Logger logger = Logger.getLogger(ChannelManagerImpl.class);
	volatile private SocketChannelFactory channelFactory;
	// key: UIR's authority, value: Socket connected to the key
	volatile private Map<UrlInfo, ChannelController> conns = Collections.synchronizedMap(new HashMap<UrlInfo, ChannelController>());
	volatile private Map<ChannelController, SocketChannel> channels = Collections.synchronizedMap(new HashMap<ChannelController, SocketChannel>());
	volatile private SocketChannelUtils utils = new SocketChannelUtils();
	volatile private boolean lockConns = false;
	
	public ChannelManagerImpl(T channelFactory) {
		this.channelFactory = channelFactory;
	}

	private void lockConns() {
		synchronized(this) {
			if(lockConns) {
				try {
					wait();
				} catch (InterruptedException e) {
					logger.warn("Thread interrupted");
				}
			} else {
				lockConns = true;
			}
		}
	}

	private void unlockConns() {
		synchronized(this) {
			lockConns = false;
			notifyAll();
		}
	}
	private void createController(UrlInfo urlInfo) {
		lockConns();
		// check again 
		// if lockConns returns from wait
		// then the Channel already created by other thread
		ChannelController controller = conns.get(urlInfo);
		if(controller == null) {
			controller = new ChannelController();
			controller.setHostname(urlInfo.getHostName());
			controller.setPort(urlInfo.getPort());
			conns.put(urlInfo, controller);
		}
		unlockConns();
	}

	private SocketChannel createChannel(ChannelController controller) {
		synchronized(controller) {
			SocketChannel channel = channels.get(controller);
			if(channel == null) {
				try {
					channel = channelFactory.createSocketChannel();
//					channel.configureBlocking(false);
					channels.put(controller, channel);
					logger.debug("create channel "+channel.hashCode());
				} catch (IOException e) {
					throw new ConnectionManagerException("Cannot create SocketChannel.", e);
				}
			}
			return channel;
		}
	}
	public SocketChannel createNewChannel(ChannelController controller) {
		synchronized(controller) {
				try {
					SocketChannel channel = channelFactory.createSocketChannel();
					channel.configureBlocking(false);
					channels.put(controller, channel);
					logger.debug("create channel "+channel.hashCode());
					return channel;
				} catch (IOException e) {
					throw new ConnectionManagerException("Cannot create SocketChannel.", e);
				}
		}
		
	}
	private void lockChannelController(ChannelController controller) {
		synchronized(controller.getLock()) {
			while(controller.isLocked()) {
				try {
					logger.debug("wait on controller "+controller.hashCode());
					controller.getLock().wait();
				} catch (InterruptedException e) {
					logger.warn("Thread interrupted");
				}
			}
			logger.debug(hashCode()+": locked controller "+controller.hashCode());
			controller.setThreadId(Thread.currentThread().getId());
		}
	}
	public ChannelController getChannelController(UrlInfo urlInfo) {
		ChannelController controller = conns.get(urlInfo);
		if(controller == null) {
			createController(urlInfo);
			controller = conns.get(urlInfo);
			if(controller == null) {
				// safety check
				throw new ConnectionManagerException("No Controller in conns Map after calling createController() method for "+urlInfo);
			}
		}
		lockChannelController(controller);
		return controller;
	}
	 
	public void releaseChannelController(ChannelController controller) {
		if(controller != null) {
			logger.debug("released controller "+controller.hashCode());
			synchronized(controller.getLock()) {
				controller.setThreadId(ChannelController.UNLOCKED);
				// bug fix: remove channel so getSocketChannel() will create a new one
				channels.remove(controller);
				controller.getLock().notifyAll();
			}
		}
	}

	public SocketChannel getChannel(ChannelController controller) {
		if(channels.containsKey(controller)) {
			return channels.get(controller);
		} else {
			return createChannel(controller);
		}
	}
	public int getConnsSize() {
		return conns.size();
	}
	
	public int getChannelsSize() {
		return channels.size();
	}
}
