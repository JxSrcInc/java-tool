package jxsource.net.http.xpath;

import jxsource.net.proxy.delegate.ProcessorFactory;
import jxsource.net.proxy.http.HttpProxyContextProvider;

public class XPathContextProvider extends HttpProxyContextProvider{

	@Override
	public ProcessorFactory getProcessorFactory() {
		return new XPathProcessorFactory();
	}

}
