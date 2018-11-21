package jxsource.net.proxy;

import java.util.HashMap;
import java.util.Map;

public class ControllerManagerImpl<T extends SocketChannelFactory> implements ControllerManager {

	private volatile Map<UrlInfo, Controller> controllers = new HashMap<UrlInfo, Controller>();
	private volatile SocketChannelFactory channelFactory;
	
	public ControllerManagerImpl(T channelFactory) {
		this.channelFactory = channelFactory;
	}

	public synchronized Controller getController(UrlInfo urlInfo) {
		if(controllers.containsKey(urlInfo)) {
			return controllers.get(urlInfo);
		} else {
			Controller controller = new Controller(channelFactory, 
					urlInfo.getHostName(), urlInfo.getPort());
			controllers.put(urlInfo, controller);
			return controller;
		}
	}
	// This function is not really used in application
	// Because application does not need to remove a controller when it is created
	// a controller life cycle will end when application terminates.
	public synchronized Controller removeController(UrlInfo urlInfo) {
		return controllers.remove(urlInfo);
	}
	public synchronized int getSize() {
		return controllers.size();
	}

}
