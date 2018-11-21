package jxsource.net.httpproxy.trace;

public interface TransferWatcherMBean {
	public int getSuccessTransaction();
	public int getWorkingTransaction();
	public int getFailTransaction();
	public int getTotalTransaction();
}
