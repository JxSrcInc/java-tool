package jxsource.net.httpsproxy.bridge;

import java.io.File;

import jxsource.net.bridge.http.HttpBridgeContextProvider;
import jxsource.net.proxy.delegate.ContextImpl;
import jxsource.net.proxy.delegate.ProxyServer;
import jxsource.net.proxy.server.ClearLog;

/*
 * Server listen to https request from browser 
 */
public class HttpsBridgeProxyServer {

	public static void main(String[] args) {
		try {
			ClearLog.clear(new File("c:/temp/log/https-bridge.log"));
			ClearLog.clearDir("c:/temp/proxyFiles/text");
			ClearLog.clearDir("c:/temp/proxyFiles/application");
			ProxyServer server = new ProxyServer();
			server.setContext(new ContextImpl(new HttpsBridgeContextProvider()));
			new Thread(server).start();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
