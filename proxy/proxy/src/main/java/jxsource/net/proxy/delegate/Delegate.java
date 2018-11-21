package jxsource.net.proxy.delegate;

import java.util.Map;

import jxsource.net.proxy.UrlInfo;

public interface Delegate {
	public Processor getProcessor(UrlInfo urlInfo);
	public void addProcessorFactory(ProcessorFactory processFactory);
}
