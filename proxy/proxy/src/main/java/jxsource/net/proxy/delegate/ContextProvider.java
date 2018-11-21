package jxsource.net.proxy.delegate;

import java.io.IOException;
import java.net.ServerSocket;

public interface ContextProvider{

	public ServerSocket getServerSocket() throws IOException;
	// return a new instance for every call
	// because Delete is not thread safe. 
	// each thread must have its own Delegate
	public Delegate getDelegate();
	public String getHost();
	public int getPort();
	public ProcessorFactory getProcessorFactory();
}
