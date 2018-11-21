package jxsource.net.httpproxy.socket;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jxsource.net.httpproxy.Config;
import jxsource.net.httpproxy.UrlInfo;

public class SingleHostSocketAccessorManager implements SocketAccessorManager {

	private volatile Map<UrlInfo, SocketAccessor> socketAccessors = Collections.synchronizedMap(new HashMap<UrlInfo, SocketAccessor>());
	public SingleHostSocketAccessorManager() {
	}

	public synchronized SocketAccessor getController(UrlInfo urlInfo) {
		String host = urlInfo.getHost();
		if(socketAccessors.containsKey(urlInfo)) {
			return socketAccessors.get(urlInfo); 
		} else {
			SocketAccessor socketAccessor = Config.getInstance().createSocketAccessor();
			socketAccessor.setUrlInfo(urlInfo);
			// Updated: select different Socket in Controller
			if(urlInfo.getProtocol().equals("https")) {
				socketAccessor.setUseSSL(true);
			}
			socketAccessors.put(urlInfo, socketAccessor);
			return socketAccessor;
		}
	}
	// This function is not really used in application
	// Because application does not need to remove a socketAccessor when it is created
	// a socketAccessor life cycle will end when application terminates.
	public synchronized SocketAccessor removeController(UrlInfo urlInfo) {
		return socketAccessors.remove(urlInfo);
	}
	public synchronized int getSize() {
		return socketAccessors.size();
	}

}
