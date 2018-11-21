package jxsource.net.http.xpath;

import jxsource.net.bridge.http.HttpBridgeContextProvider;
import jxsource.net.proxy.delegate.ContextImpl;
import jxsource.net.proxy.delegate.ProxyServer;

public class XPathProxyServer {

	public static void main(String[] args) {
		try {
			ProxyServer server = new ProxyServer();
			server.setContext(new ContextImpl(new XPathContextProvider()));
			new Thread(server).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
