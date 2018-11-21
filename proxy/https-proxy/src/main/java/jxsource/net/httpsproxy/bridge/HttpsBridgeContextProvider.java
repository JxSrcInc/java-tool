package jxsource.net.httpsproxy.bridge;

import java.io.IOException;
import java.net.ServerSocket;

import jxsource.net.proxy.DefaultSocketFactory;
import jxsource.net.proxy.delegate.ContextProvider;
import jxsource.net.proxy.delegate.DefaultDelegate;
import jxsource.net.proxy.delegate.Delegate;
import jxsource.net.proxy.delegate.ProcessorFactory;

public class HttpsBridgeContextProvider implements ContextProvider{

	public ProcessorFactory getProcessorFactory() {
		return new HttpsBridgeProcessorFactory();
	}

	public ServerSocket getServerSocket() throws IOException {
		return new DefaultSocketFactory().createSSLServerSocket();
	}

	public Delegate getDelegate() {
		return new DefaultDelegate();
	}

	public String getHost() {
		return "localhost";
	}

	public int getPort() {
		return 10090;
	}

}
