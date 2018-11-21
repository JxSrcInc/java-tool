package jxsource.net.proxy.exception;

public class RemoteConnectionException extends ConnectStopException {
	private static final long serialVersionUID = 1L;

	public RemoteConnectionException() {
		super();
	}
	public RemoteConnectionException(Throwable throwable) {
		super(throwable);
	}
	public RemoteConnectionException(String message) {
		super(message);
	}
	public RemoteConnectionException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
