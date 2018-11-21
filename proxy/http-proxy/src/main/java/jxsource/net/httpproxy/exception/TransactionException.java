package jxsource.net.httpproxy.exception;

import jxsource.net.httpproxy.trace.TransferTrace;

public class TransactionException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public final TransferTrace traceable;
	public TransactionException(String message, TransferTrace traceable) {
		super(message);
		this.traceable = traceable;
	}
	public TransferTrace getTrace() {
		return traceable;
	}
}
