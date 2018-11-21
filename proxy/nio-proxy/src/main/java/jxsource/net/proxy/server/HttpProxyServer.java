package jxsource.net.proxy.server;

import jxsource.net.proxy.BridgeServer;
import jxsource.net.proxy.http.HttpExecutorFactory;

public class HttpProxyServer {

	public static void main(String[] args) {
		try {
			BridgeServer server = new BridgeServer();
			server.setExecutorFactory(new HttpExecutorFactory());
			server.init();
			new Thread(server).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
