package jxsource.net.proxy.http.exception;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

public class MessageHeaderException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String channelInfo;
	private void createChannelInfo(SocketChannel channel) {
		try {
			channelInfo = "" + channel.getRemoteAddress();
		} catch (IOException e) {
			channelInfo = ""+e.getMessage();
		}
	}
	public MessageHeaderException(SocketChannel channel) {
		createChannelInfo(channel);
	}
	public MessageHeaderException(String message, SocketChannel channel) {
		super(message);
		createChannelInfo(channel);
	}
	public MessageHeaderException(Throwable throwable, SocketChannel channel) {
		super(throwable);
		createChannelInfo(channel);
	}
	public MessageHeaderException(String message, Throwable throwable, SocketChannel channel) {
		super(message, throwable);
		createChannelInfo(channel);
	}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return channelInfo + ": "+ super.getMessage();
	}
}
