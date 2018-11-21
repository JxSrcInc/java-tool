package jxsource.net.httpsproxy.proxy;

public class TransferException extends RuntimeException {

	public TransferException() {
		super();
	}
	public TransferException(String msg) {
		super(msg);
	}
	public TransferException(String msg, Throwable t) {
		super(msg, t);
	}
	public TransferException(Throwable t) {
		super(t);
	}

}
