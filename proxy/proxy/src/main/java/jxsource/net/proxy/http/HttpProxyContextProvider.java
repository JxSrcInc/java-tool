package jxsource.net.proxy.http;

import java.io.IOException;
import java.net.ServerSocket;

import jxsource.net.proxy.DefaultSocketFactory;
import jxsource.net.proxy.delegate.ContextProvider;
import jxsource.net.proxy.delegate.DefaultDelegate;
import jxsource.net.proxy.delegate.Delegate;
import jxsource.net.proxy.delegate.ProcessorFactory;

public class HttpProxyContextProvider implements ContextProvider{

	public ProcessorFactory getProcessorFactory() {
		return new HttpProcessorFactory();
	}

	public ServerSocket getServerSocket() throws IOException {
		return new DefaultSocketFactory().createServerSocket();
	}

	public Delegate getDelegate() {
		return new DefaultDelegate();
	}

	public String getHost() {
		return "localhost";
	}

	public int getPort() {
		return 10080;
	}

}
