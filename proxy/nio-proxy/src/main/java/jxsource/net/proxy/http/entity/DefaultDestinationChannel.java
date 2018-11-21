package jxsource.net.proxy.http.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import jxsource.net.proxy.Utils;

public class DefaultDestinationChannel implements EntityDestinationChannel{
	private static int index = 0;
	SocketChannel channel;
	Selector selector;
	Utils utils = new Utils();
	
	long len = 0;
	public DefaultDestinationChannel() {
	}
	
	public SocketChannel getChannel() {
		return channel;
	}
	public void setSocketChannel(SocketChannel channel) {
		if(this.channel == null) {
			this.channel = channel;
		} else
		if(this.channel != channel) {
			// Shouldn't happen
			System.err.println("Different channels: existing="+this.channel+", replace="+channel);
			// Override existing channel. TODO: handle in different ways?
			this.channel = channel;
		}
	}
	
	public SelectionKey register(Selector selector, int options) throws ClosedChannelException {
		this.selector = selector;
		return channel.register(selector, options);
	}
	
	public void write(ByteBuffer buffer) throws IOException {
		int position = buffer.position();
		len += position;
		buffer.flip();
//		channel.write(buffer);
		utils.transfer(buffer, channel);
	}
	
	public void close() {
//			System.out.println("write "+len+" bytes");
	}


}
