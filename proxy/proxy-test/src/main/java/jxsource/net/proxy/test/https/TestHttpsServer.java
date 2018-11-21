package jxsource.net.proxy.test.https;

import jxsource.net.proxy.delegate.ProxyServer;

public class TestHttpsServer extends Thread{

	public static void main(String[] args) {
		try {
			ProxyServer server = new ProxyServer();
			server.setContext(new TestHttpsServerFactory());
			new Thread(server).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
