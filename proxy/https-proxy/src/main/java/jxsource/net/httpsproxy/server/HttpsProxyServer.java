package jxsource.net.httpsproxy.server;

import jxsource.net.httpsproxy.proxy.ConnectorFactory;
import jxsource.net.httpsproxy.proxy.SSLProxyServer;

public class HttpsProxyServer {

	public static void main(String[] args) {
		try {
			SSLProxyServer server = new SSLProxyServer();
			server.setConnectorFactory(new ConnectorFactory());
			server.init();
			new Thread(server).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
