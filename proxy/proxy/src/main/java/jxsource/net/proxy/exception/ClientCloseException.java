package jxsource.net.proxy.exception;

public class ClientCloseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ClientCloseException() {
	}
	public ClientCloseException(Exception e) {
		super(e);
	}
	public ClientCloseException(String message) {
		super(message);
	}
	public ClientCloseException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
