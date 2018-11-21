package jxsource.net.proxy.exception;

public class TransferException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TransferException() {
	}
	public TransferException(Exception e) {
		super(e);
	}
	public TransferException(String message) {
		super(message);
	}
	public TransferException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
