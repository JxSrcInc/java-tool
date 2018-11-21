package jxsource.net.proxy.exception;

public class ConnectionManagerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConnectionManagerException() {
	}
	public ConnectionManagerException(Exception e) {
		super(e);
	}
	public ConnectionManagerException(String message) {
		super(message);
	}
	public ConnectionManagerException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
