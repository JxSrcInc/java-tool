package jxsource.net.httpproxy.exception;

public class ProxyClientError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProxyClientError() {
	}
	public ProxyClientError(Exception e) {
		super(e);
	}
	public ProxyClientError(String message) {
		super(message);
	}
	public ProxyClientError(String message, Throwable throwable) {
		super(message, throwable);
	}

}
