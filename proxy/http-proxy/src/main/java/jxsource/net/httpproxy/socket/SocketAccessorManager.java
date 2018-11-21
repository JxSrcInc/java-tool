package jxsource.net.httpproxy.socket;

import jxsource.net.httpproxy.UrlInfo;

public interface SocketAccessorManager {

	public SocketAccessor getController(UrlInfo urlInfo);
	// This function is not really used in application
	// Because application does not need to remove a controller when it is created
	// a controller life cycle will end when application terminates.
	public SocketAccessor removeController(UrlInfo urlInfo);
	public int getSize();

}
