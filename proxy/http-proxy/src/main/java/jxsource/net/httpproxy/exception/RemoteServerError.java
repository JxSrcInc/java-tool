package jxsource.net.httpproxy.exception;

public class RemoteServerError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RemoteServerError() {
	}
	public RemoteServerError(Exception e) {
		super(e);
	}
	public RemoteServerError(String message) {
		super(message);
	}
	public RemoteServerError(String message, Throwable throwable) {
		super(message, throwable);
	}

}
