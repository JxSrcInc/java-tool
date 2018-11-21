package jxsource.net.proxy.app;


public class HttpChannelTestApp{
	public static void main(String[] args) {
		String[] urls = new String[] {
				"http://localhost:10090/HeaderOnly",
				"http://localhost",
				"http://localhost:10090/Content-Length"
		};
		ChannelTestManager mgr = new ChannelTestManager();
		HttpChannelRunnable hcr1 = new HttpChannelRunnable(mgr, urls);
		HttpChannelRunnable hcr2 = new ModifyHttpChannelRunnable(mgr, urls);
		HttpChannelRunnable hcr3 = new HttpChannelRunnable(mgr, urls);
		hcr1.setProxy("localhost", 10080);
		hcr2.setProxy("localhost", 10080);
		hcr3.setProxy("localhost", 10080);
		new Thread(hcr1, "HCR-1").start();
		new Thread(hcr2, "MHCR-2").start();
		new Thread(hcr3, "HCR-3").start();
	}
}
