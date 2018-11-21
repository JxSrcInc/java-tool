package jxsource.net.proxy.delegate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jxsource.net.proxy.UrlInfo;

/*
 * DefaultDelegate has only one Processer for all urlInfo
 */
public class DefaultDelegate implements Delegate {
	private Processor processor;
	
	public Processor getProcessor(UrlInfo urlInfo) {
		return processor;
	}
	
	public void addProcessorFactory(ProcessorFactory processorFactory) {
		processor = processorFactory.createProcessor();
		
	}

}
