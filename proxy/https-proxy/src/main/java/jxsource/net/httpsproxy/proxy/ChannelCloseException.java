package jxsource.net.httpsproxy.proxy;

public class ChannelCloseException extends RuntimeException {

	public ChannelCloseException() {
		super();
	}
	public ChannelCloseException(String msg) {
		super(msg);
	}
	public ChannelCloseException(String msg, Throwable t) {
		super(msg, t);
	}
	public ChannelCloseException(Throwable t) {
		super(t);
	}

}
