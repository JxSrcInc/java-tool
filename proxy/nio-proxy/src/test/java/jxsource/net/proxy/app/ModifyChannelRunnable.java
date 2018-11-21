package jxsource.net.proxy.app;

import static org.junit.Assert.assertTrue;

import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import jxsource.net.proxy.ChannelController;
import jxsource.net.proxy.UrlInfo;

public class ModifyChannelRunnable extends ChannelRunnable {

	public ModifyChannelRunnable(ChannelTestManager mgr) {
		super(mgr);
		logger = Logger.getLogger(ModifyChannelRunnable.class);
	}
	@Override
	public void run() {
		for(int i=0; i<loopSize; i++) {
			if(i==1 || i==3) {
				newChannel(new UrlInfo("http://localhost"));
			} else {
				transaction(new UrlInfo("http://localhost"));
			}
		}
	}
	
	protected void newChannel(UrlInfo urlInfo) {
		ChannelController controller = mgr.getChannelManager().getChannelController(urlInfo);
		SocketChannel workingChannel = mgr.getChannelManager().createNewChannel(controller);
		mgr.setWorkingChannel(workingChannel);
		logger.debug("******* create new channel "+workingChannel.hashCode());
		mgr.getChannelManager().releaseChannelController(controller);
		
	}


}
