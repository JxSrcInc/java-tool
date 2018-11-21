package jxsource.net.proxy;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public interface ConnectionManager {
	public SocketChannel getChannel(UrlInfo urlInfo);
//	@Deprecated 
//	public SocketChannel removeChannel(UrlInfo urlInfo);
	public void releaseChannel(SocketChannel channel);
	public void clean(Selector... selector);
}
