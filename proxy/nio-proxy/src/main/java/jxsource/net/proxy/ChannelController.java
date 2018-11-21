package jxsource.net.proxy;

import org.apache.log4j.Logger;

public class ChannelController {
	private static Logger logger = Logger.getLogger(ChannelController.class);
	public static final long UNLOCKED = -1;
	private long threadId = UNLOCKED;
	private Object lock = new Object();
	private String hostname;
	private int port;
	public long getThreadId() {
		return threadId;
	}
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}
	public Object getLock() {
		return lock;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public boolean isLocked() {
		if(threadId == UNLOCKED || threadId == Thread.currentThread().getId()) {
			return false;
		} else {
			return true;
		}
	}

}
