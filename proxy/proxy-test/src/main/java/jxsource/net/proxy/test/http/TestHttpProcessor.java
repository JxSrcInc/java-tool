package jxsource.net.proxy.test.http;

import jxsource.net.proxy.delegate.Processor;
import jxsource.net.proxy.delegate.RequestHandler;
import jxsource.net.proxy.http.RequestInfo;

public class TestHttpProcessor implements Processor {
	
	RequestHandler requestHandler = new TestHttpRequestHandler();
	public TestHttpProcessor() {
		// TODO Auto-generated constructor stub
	}

	public RequestHandler getRequestHandler() {
		return requestHandler;
	}
	public void handleRequest(RequestInfo requestInfo) {
		((TestHttpRequestHandler)requestHandler).run(requestInfo);
	}

}
