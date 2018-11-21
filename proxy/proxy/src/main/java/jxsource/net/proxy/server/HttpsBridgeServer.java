package jxsource.net.proxy.server;

import java.io.File;

import jxsource.net.bridge.http.HttpBridgeContextProvider;
import jxsource.net.proxy.delegate.ContextImpl;
import jxsource.net.proxy.delegate.ProxyServer;

/*
 * this class support both HTTP and HTTPS.
 * 
 * Usage: http://bridge__[<protocol>__host__]<hostname>[__port__<port>]__bridge
 * Example: 
 * 		http://bridge__localhost__bridge
 * 		http://bridge__https__host__www.googol.com__bridge
 * 		http://bridge__https__host__www.googol.com__port__443__bridge
 */
public class HttpsBridgeServer {

	public static void main(String[] args) {
		try {
			ClearLog.clear(new File("c:/temp/log/proxy.log"));
			ClearLog.clearDir("c:/temp/proxyFiles/text");
			ClearLog.clearDir("c:/temp/proxyFiles/application");
			ProxyServer server = new ProxyServer();
			server.setContext(new ContextImpl(new HttpBridgeContextProvider()));
			new Thread(server).start();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
