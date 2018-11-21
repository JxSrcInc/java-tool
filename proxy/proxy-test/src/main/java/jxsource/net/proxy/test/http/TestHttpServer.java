package jxsource.net.proxy.test.http;

import jxsource.net.proxy.delegate.ProxyServer;

public class TestHttpServer {

	public static void main(String[] args) {
		try {
//			BridgeServer server = new BridgeServer();
//			server.setExecutorFactory(new TestHttpExecutorFactory());
			ProxyServer server = new ProxyServer();
			server.setContext(new TestHttpServerFactory());
			new Thread(server).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
