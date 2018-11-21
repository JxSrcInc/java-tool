package jxsource.net.proxy.app;

import java.nio.channels.SocketChannel;
import java.util.Random;

import jxsource.net.proxy.ChannelController;
import jxsource.net.proxy.ChannelManager;
import jxsource.net.proxy.ChannelManagerImpl;
import jxsource.net.proxy.PlainSocketChannelFactory;

import org.apache.log4j.Logger;

public class ChannelTestManager {
	private static Logger logger = Logger.getLogger(ChannelTestManager.class);
	private volatile ChannelManager channelManager
		= new ChannelManagerImpl<PlainSocketChannelFactory>(new PlainSocketChannelFactory());
	private volatile ChannelController workingController;
	private volatile SocketChannel workingChannel;
	Random randomGenerator = new Random();

	public synchronized ChannelManager getChannelManager() {
		return channelManager;
	}

	public synchronized void setChannelManager(
			ChannelManager channelManager) {
		this.channelManager = channelManager;
	}

	public synchronized ChannelController getWorkingController() {
		return workingController;
	}

	public synchronized void setWorkingController(ChannelController workingController) {
		this.workingController = workingController;
	}

	public synchronized SocketChannel getWorkingChannel() {
		return workingChannel;
	}

	public synchronized void setWorkingChannel(SocketChannel workingChannel) {
		this.workingChannel = workingChannel;
	}

	public void sleep() {
		try {
			int sleepTime = randomGenerator.nextInt(1000);
			logger.debug("sleep "+sleepTime);
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
