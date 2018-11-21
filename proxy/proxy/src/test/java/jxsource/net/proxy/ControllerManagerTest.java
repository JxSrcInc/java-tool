package jxsource.net.proxy;

import jxsource.net.proxy.app.ControllerRunnableModifier;
import jxsource.net.proxy.app.ControllerManagerTester;
//import jxsource.net.proxy.app.ModifyChannelRunnable;
import jxsource.net.proxy.app.ControllerRunnable;

import org.junit.Test;

public class ControllerManagerTest {

	@Test
	public void simpleTest() throws InterruptedException {
		ControllerManagerTester mgr = new ControllerManagerTester();
		Thread t10 = new Thread(new ControllerRunnable(mgr), "SCR-1");
		Thread t1000 = new Thread(new ControllerRunnableModifier(mgr), "MCR-2");
		Thread t50 = new Thread(new ControllerRunnable(mgr), "SCR-3");

		t10.start();
		t1000.start();
		t50.start();
		t10.join();
		t1000.join();
		t50.join();
	}	

}
