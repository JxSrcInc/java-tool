package jxsource.net.proxy.app;

import static org.junit.Assert.assertTrue;

import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import jxsource.net.proxy.ChannelController;
import jxsource.net.proxy.UrlInfo;

public class ChannelRunnable implements Runnable{
	protected Logger logger;

	protected ChannelTestManager mgr;
	protected int loopSize = 5;
	public ChannelRunnable(ChannelTestManager mgr) {
		this.mgr = mgr;
		logger = Logger.getLogger(ChannelRunnable.class);
	}
	public void setLoopSize(int loopSize) {
		this.loopSize = loopSize;
	}
	public void run() {
		for(int i=0; i<loopSize; i++) {
			transaction(new UrlInfo("http://localhost"));
		}
	}
	protected void transaction(UrlInfo urlInfo) {
		ChannelController controller = mgr.getChannelManager().getChannelController(urlInfo);
		if(mgr.getWorkingController() == null) {
			mgr.setWorkingController(controller);
		}
		logger.debug("get controller: "+controller.hashCode());
		assertTrue("different controller", mgr.getWorkingController() == controller);
//		SocketChannel channel = cm.getChannel(controller);
//		logger.debug("get Channel "+channel.hashCode()+","+channel);
		mgr.sleep();
		SocketChannel channel = mgr.getChannelManager().getChannel(controller);
		if(mgr.getWorkingChannel() == null) {
			mgr.setWorkingChannel(channel);
		}
		logger.debug("get channel: "+channel.hashCode());
		assertTrue("different channel", mgr.getWorkingChannel() == channel);
		mgr.getChannelManager().releaseChannelController(controller);
		
	}
	

}
