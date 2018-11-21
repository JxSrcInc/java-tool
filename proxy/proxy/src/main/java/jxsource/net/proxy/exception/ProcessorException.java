package jxsource.net.proxy.exception;

public class ProcessorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProcessorException() {
	}
	public ProcessorException(Exception e) {
		super(e);
	}
	public ProcessorException(String message) {
		super(message);
	}
	public ProcessorException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
