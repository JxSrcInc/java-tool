package jxsource.net.proxy.app;


public class ChannelManagerTestApp{
	public static void main(String[] args) {
		ChannelTestManager mgr = new ChannelTestManager();
		new Thread(new ChannelRunnable(mgr), "SCR-1").start();
		new Thread(new ModifyChannelRunnable(mgr), "MCR-2").start();
		new Thread(new ChannelRunnable(mgr), "SCR-3").start();
	}
}
