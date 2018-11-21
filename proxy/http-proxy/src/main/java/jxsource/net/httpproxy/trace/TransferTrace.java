package jxsource.net.httpproxy.trace;

import java.util.ArrayList;
import java.util.List;

import jxsource.net.httpproxy.ProxyTransferProcessor;
import jxsource.net.httpproxy.RequestInfo;
import jxsource.net.httpproxy.ResponseInfo;
import jxsource.net.httpproxy.UrlInfo;

public class TransferTrace{

	public static final int WORKING = 0;
	public static final int SUCCESS = 1;
	public static final int FAIL = -1;
	private List<Log> logs = new ArrayList<Log>();
	private RequestInfo requestInfo;
	private int status;
	private String threadName;
	private long startTime;
	private long duration;
	

	
	public TransferTrace(ProxyTransferProcessor proxyTransferProcessor) {
		threadName = Thread.currentThread().getName();
		startTime = System.currentTimeMillis();
		requestInfo =  proxyTransferProcessor.getReceivedRequestInfo();
	}

	public void add(Log log) {
		logs.add(log);
	}
	public void complate(int status) {
		this.status = status;
		duration = System.currentTimeMillis() - startTime;
	}
	
	public String getLogStr() {
		String msg = "";
		for (int i = 0; i < logs.size(); i++) {
			Log log = logs.get(i);
			msg += log.toString()+"\n\t\t";
			if(log.value instanceof Throwable) {
				Throwable t = ((Throwable)log.value).getCause();
				if(t != null) {
					msg += t;
				}
			}
			if (i < logs.size() - 1) {
				msg += "\n\t\t";
			}
		}
		return msg;
	}

	public int getStatus() {
		return status;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public String getThreadName() {
		return threadName;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getDuration() {
		return duration;
	}

}
