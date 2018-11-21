package jxsource.net.proxy.test.http;

import jxsource.net.proxy.delegate.Processor;
import jxsource.net.proxy.delegate.ProcessorFactory;
import jxsource.net.proxy.http.HttpProcessor;

public class TestHttpProcessorFactory implements ProcessorFactory {

	public Processor createProcessor() {
		TestHttpProcessor processor = new TestHttpProcessor();
		return processor;
	}

}
