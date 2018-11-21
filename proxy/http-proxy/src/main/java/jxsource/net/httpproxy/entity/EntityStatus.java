package jxsource.net.httpproxy.entity;

import jxsource.net.httpproxy.Constants;

public class EntityStatus {
	private long processedBytes;
	private long requestedBytes = Constants.InfiniteLong;
	private String status;
	public EntityStatus(long processedBytes, String status, long requestedBytes) {
		this.requestedBytes = requestedBytes;
		this.processedBytes = processedBytes;
		this.status = status;
	}
	// for chunked entity
	public EntityStatus(long processedBytes, String status) {
		this.processedBytes = processedBytes;
		this.status = status;
	}

	public long getProcessedBytes() {
		return processedBytes;
	}
	public void setProcessedBytes(long processedBytes) {
		this.processedBytes = processedBytes;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getRequestedBytes() {
		return requestedBytes;
	}
	public void setRequestedBytes(long requestedBytes) {
		this.requestedBytes = requestedBytes;
	}
	@Override
	public String toString() {
		String requestedBytesString = ""+requestedBytes;
		if(requestedBytes == Constants.InfiniteLong) {
			requestedBytesString = "infinite";
		}
		return "EntityStatus [status=" + status + ", processedBytes="
				+ processedBytes + ", requestedBytes=" + requestedBytesString + "]";
	}
	
}
