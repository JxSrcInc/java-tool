package jxsource.net.proxy.exception;

public class LocalRequestException extends ConnectStopException {
	private static final long serialVersionUID = 1L;

	public LocalRequestException() {
	}
	public LocalRequestException(Exception e) {
		super(e);
	}
	public LocalRequestException(String message) {
		super(message);
	}
	public LocalRequestException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
