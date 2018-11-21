package jxsource.net.proxy.delegate;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import jxsource.net.proxy.AppProperties;
import jxsource.net.proxy.Constants;

public class ContextImpl implements Context{

	private ContextProvider provider;

	public ContextImpl(ContextProvider provider) {
		if(provider == null) {
			throw new RuntimeException("ContextImpl Error: ContextProvider cannot be null.");
		}
		this.provider = provider;
		setAppProperties();
	}
	private void setAppProperties() {
		AppProperties appProperties = AppProperties.getInstance();
		String propertiesFile = System.getProperty("jxsource.net.proxy.appproperties");
		if(propertiesFile != null && new File(propertiesFile).exists()) {
			appProperties.loadAppProperties(propertiesFile);
		} else {
			File file = new File(Constants.defaultAppPropertiesFile);
			if(file.exists()) {
				appProperties.loadAppProperties(Constants.defaultAppPropertiesFile);
			}
		}
	}
	public ContextProvider getProvider() {
		return provider;
	}
	public Delegate getDelegate() {
		// Each thread has its Delegate
		Delegate delegate = provider.getDelegate();
		// But all delegats have the same application-level ProcessoreFactory
		delegate.addProcessorFactory(provider.getProcessorFactory());
		return delegate;
	}
	public ServerSocket getServerSocket() throws IOException {
		return provider.getServerSocket();
	}
	public String getHost() {
		return provider.getHost();
	}
	public int getPort() {
		return provider.getPort();
	}

}
