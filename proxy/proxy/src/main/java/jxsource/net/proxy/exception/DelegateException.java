package jxsource.net.proxy.exception;

public class DelegateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DelegateException() {
	}
	public DelegateException(Exception e) {
		super(e);
	}
	public DelegateException(String message) {
		super(message);
	}
	public DelegateException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
