package jxsource.net.proxy.http.exception;

public class EntityException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EntityException() {
	}
	public EntityException(Exception e) {
		super(e);
	}
	public EntityException(String message) {
		super(message);
	}
	public EntityException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
