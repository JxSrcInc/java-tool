package jxsource.net.httpproxy.exception;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

public class MessageHeaderException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public MessageHeaderException() {
		super();
	}
	public MessageHeaderException(String message) {
		super(message);
	}
	public MessageHeaderException(Throwable throwable) {
		super(throwable);
	}
	public MessageHeaderException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
