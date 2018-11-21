package jxsource.net.proxy.server;

import jxsource.net.proxy.delegate.ContextImpl;
import jxsource.net.proxy.delegate.ProxyServer;
import jxsource.net.proxy.http.HttpProxyContextProvider;

public class HttpProxyServer {

	public static void main(String[] args) {
		try {
//			BridgeServer server = new BridgeServer();
//			server.setExecutorFactory(new HttpExecutorFactory());

			ProxyServer server = new ProxyServer();
			server.setContext(new ContextImpl(new HttpProxyContextProvider()));
			new Thread(server).start();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
