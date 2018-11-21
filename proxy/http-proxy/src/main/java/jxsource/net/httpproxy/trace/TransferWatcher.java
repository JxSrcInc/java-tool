package jxsource.net.httpproxy.trace;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import jxsource.net.httpproxy.ProxyTransferProcessor;

public class TransferWatcher implements TransferWatcherMBean{

	private static TransferWatcher me;
	private Set<TransferTrace> tracers = 
			Collections.synchronizedSet(new HashSet<TransferTrace>());
	private int totalCount;
	private Map<String, Integer> responseStatus = new HashMap<String, Integer>();
	private String logFile = "c:/temp/watcher.log";
	
	public static TransferWatcher getInstance() {
		if(me == null) {
			me = new TransferWatcher();
		}
		return me;
	}
	public void add(TransferTrace tracer) {
		totalCount++;
		tracers.add(tracer);
	}
	public int getProcessorCount() {
		return tracers.size();
	}
	public int getWorkingTransaction() {
		int count = 0;
		for(TransferTrace trace: tracers) {
			if(trace.getStatus() ==  TransferTrace.WORKING) {
				count++;
			}
		}
		this.output(TransferTrace.WORKING);
		return count;
	}
	public int getSuccessTransaction() {
		int count = 0;
		for(TransferTrace trace: tracers) {
			if(trace.getStatus() ==  TransferTrace.SUCCESS) {
				count++;
			}
		}
		return count;
	}
	public int getFailTransaction() {
		int count = 0;
		for(TransferTrace trace: tracers) {
			if(trace.getStatus() ==  TransferTrace.FAIL) {
				count++;
			}
		}
		return count;
	}
	public int getTotalTransaction() {
		return totalCount;
	}
	
	private void output(int status) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(this.logFile));
			for(TransferTrace trace: tracers) {
				if(trace.getStatus() ==  status) {
					printTrace(trace, out);
				}
			}
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getStatusName(int status) {
		switch(status) {
		case 1:
			return "Success";
		case 0:
			return "Working";
		case -1:
			return "Fail";
			default:
				return "Unknown";
		}
	}
	private void printTrace(TransferTrace trace, PrintStream out) {
		out.println(trace.getThreadName());
		out.println(System.currentTimeMillis()-trace.getStartTime());
		out.println(getStatusName(trace.getStatus()));
		out.println(trace.getRequestInfo().getRequest());
		out.flush();
	}
}
