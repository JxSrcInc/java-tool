package jxsource.net.proxy.exception;

public class RemoteServerCloseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RemoteServerCloseException() {
	}
	public RemoteServerCloseException(Exception e) {
		super(e);
	}
	public RemoteServerCloseException(String message) {
		super(message);
	}
	public RemoteServerCloseException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
