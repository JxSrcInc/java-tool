package jxsource.net.proxy;

import jxsource.net.proxy.app.ChannelManagerTestApp;
import jxsource.net.proxy.app.ChannelTestManager;
import jxsource.net.proxy.app.ModifyChannelRunnable;
import jxsource.net.proxy.app.ChannelRunnable;

import org.junit.Test;

public class ChannelManagerTest {

	@Test
	public void simpleTest() throws InterruptedException {
		ChannelTestManager mgr = new ChannelTestManager();
		Thread t10 = new Thread(new ChannelRunnable(mgr), "SCR-1");
		Thread t1000 = new Thread(new ModifyChannelRunnable(mgr), "MCR-2");
		Thread t50 = new Thread(new ChannelRunnable(mgr), "SCR-3");

		t10.start();
		t1000.start();
		t50.start();
		t10.join();
		t1000.join();
		t50.join();
	}	

}
