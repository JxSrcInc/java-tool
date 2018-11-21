package jxsource.net.proxy.http;

import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.ControllerManagerImpl;
import jxsource.net.proxy.DefaultSocketFactory;
import jxsource.net.proxy.delegate.Processor;
import jxsource.net.proxy.delegate.ProcessorFactory;
import jxsource.net.proxy.http.entity.ProxyHttpEntityDestinationSocketManager;

public class HttpProcessorFactory implements ProcessorFactory {
	
	private ControllerManager controllerManager = new ControllerManagerImpl<DefaultSocketFactory>(
			new DefaultSocketFactory());
	
	public Processor createProcessor() {
		// Each call gets a new Processor
		HttpProcessor processor = new HttpProcessor();
		HttpRequestHandler handler = new HttpRequestHandler(); 
		// Each handler has its EntityDestinationSocketManager instance
		handler.setEntityDestinationSocketManager(new ProxyHttpEntityDestinationSocketManager());
		// But all handlers have the same ControllerManager
		handler.setControllerManager(controllerManager);
// each handler has its own ControllerManager		
//		handler.setControllerManager(new ControllerManagerImpl<DefaultSocketFactory>(
//				new DefaultSocketFactory()));
		processor.setRequestHandler(handler);
		return processor;
	}
	
}
