package jxsource.net.proxy;

import java.nio.channels.SocketChannel;

public interface ChannelManager {

	public SocketChannel createNewChannel(ChannelController controller);
	public ChannelController getChannelController(UrlInfo urlInfo);
	public void releaseChannelController(ChannelController controller);
	public SocketChannel getChannel(ChannelController controller);
	public int getConnsSize();
	public int getChannelsSize();
}
