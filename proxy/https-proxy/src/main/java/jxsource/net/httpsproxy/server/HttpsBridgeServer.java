package jxsource.net.httpsproxy.server;

import java.io.File;

import jxsource.net.httpsproxy.bridge.BridgeConnectorFactory;
import jxsource.net.httpsproxy.proxy.ConnectorFactory;
import jxsource.net.httpsproxy.proxy.SSLProxyServer;
import jxsource.net.proxy.server.ClearLog;
 
/*
 * Server listen to https request from browser 
 */
public class HttpsBridgeServer {

	public static void main(String[] args) {
		try {
			ClearLog.clear(new File("c:/temp/log/https-proxy.log"));
			ClearLog.clearDir("c:/temp/proxyFiles/text");
			ClearLog.clearDir("c:/temp/proxyFiles/application");
			SSLProxyServer server = new SSLProxyServer();
			server.setConnectorFactory(new BridgeConnectorFactory());
			server.init();
			new Thread(server).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
