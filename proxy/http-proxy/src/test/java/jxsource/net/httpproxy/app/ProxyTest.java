package jxsource.net.httpproxy.app;

import jxsource.net.httpproxy.ProxyServer;

public class ProxyTest {
	public static void main(String[] args) {
		ProxyServer server = new ProxyServer();
		server.start("src/test/resource/test.cfg");
	}

}
