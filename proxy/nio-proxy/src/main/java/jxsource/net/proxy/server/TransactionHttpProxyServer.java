package jxsource.net.proxy.server;

import jxsource.net.proxy.BridgeServer;
import jxsource.net.proxy.TransactionBridgeServer;
import jxsource.net.proxy.http.HttpTransactionFactory;

public class TransactionHttpProxyServer {

	public static void main(String[] args) {
		try {
			TransactionBridgeServer server = new TransactionBridgeServer();
			server.setExecutorFactory(new HttpTransactionFactory());
			server.init();
			new Thread(server).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
