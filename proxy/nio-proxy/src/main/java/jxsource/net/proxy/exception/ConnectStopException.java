package jxsource.net.proxy.exception;

public class ConnectStopException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConnectStopException() {
	}
	public ConnectStopException(String message) {
		super(message);
	}
	public ConnectStopException(Throwable throwable) {
		super(throwable);
	}
	public ConnectStopException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
