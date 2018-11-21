package jxsource.net.proxy.exception;

public class RemoteResponseException extends ConnectStopException {
	private static final long serialVersionUID = 1L;

	public RemoteResponseException() {
	}
	public RemoteResponseException(Throwable throwable) {
		super(throwable);
	}
	public RemoteResponseException(String message) {
		super(message);
	}
	public RemoteResponseException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
