package jxsource.net.proxy.server;

import jxsource.net.proxy.BridgeServer;
import jxsource.net.proxy.http.HttpExecutorFactory;
import jxsource.net.proxy.https.HttpsExecutorFactory;

public class HttpsProxyServer {

	public static void main(String[] args) {
		try {
			BridgeServer server = new BridgeServer();
			server.setExecutorFactory(new HttpsExecutorFactory());
			server.init();
			new Thread(server).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
