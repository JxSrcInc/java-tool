package jxsource.net.proxy.http.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public interface EntityDestinationChannel {
	public SocketChannel getChannel();
	public void setSocketChannel(SocketChannel channel);
	public SelectionKey register(Selector selector, int options) throws ClosedChannelException;
	public void write(ByteBuffer buffer) throws IOException;
	public void close();
}
