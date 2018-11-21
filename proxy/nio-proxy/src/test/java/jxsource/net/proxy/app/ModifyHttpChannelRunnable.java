package jxsource.net.proxy.app;

import static org.junit.Assert.assertTrue;

import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import jxsource.net.proxy.ChannelController;
import jxsource.net.proxy.UrlInfo;

public class ModifyHttpChannelRunnable extends HttpChannelRunnable {

	public ModifyHttpChannelRunnable(ChannelTestManager mgr, String[] urls) {
		super(mgr, urls);
		logger = Logger.getLogger(ModifyHttpChannelRunnable.class);
	}
	@Override
	public void run() {
		for(int i=0; i<urls.length; i++) {
			newChannel(new UrlInfo(urls[i]));
			transaction(urls[i]);
		}
	}
	
	protected void newChannel(UrlInfo urlInfo) {
		
		ChannelController controller = mgr.getChannelManager().getChannelController(urlInfo);
		if(controller != null) {
		SocketChannel workingChannel = mgr.getChannelManager().createNewChannel(controller);
		mgr.setWorkingChannel(workingChannel);
		logger.debug("####### create new channel "+workingChannel.hashCode());
		mgr.getChannelManager().releaseChannelController(controller);
		}
		
	}


}
