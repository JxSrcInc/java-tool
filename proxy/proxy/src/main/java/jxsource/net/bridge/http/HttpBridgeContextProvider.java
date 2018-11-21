package jxsource.net.bridge.http;

import java.io.IOException;
import java.net.ServerSocket;

import jxsource.net.proxy.DefaultSocketFactory;
import jxsource.net.proxy.delegate.ContextProvider;
import jxsource.net.proxy.delegate.DefaultDelegate;
import jxsource.net.proxy.delegate.Delegate;
import jxsource.net.proxy.delegate.ProcessorFactory;

public class HttpBridgeContextProvider implements ContextProvider{

	public ProcessorFactory getProcessorFactory() {
		return new HttpBridgeProcessorFactory();
	}

	public ServerSocket getServerSocket() throws IOException {
		return new DefaultSocketFactory().createServerSocket();
	}

	public Delegate getDelegate() {
		// Each thread has its Delegate
		return new DefaultDelegate();
	}

	public String getHost() {
		return "localhost";
	}

	public int getPort() {
		return 10080;
	}

}
