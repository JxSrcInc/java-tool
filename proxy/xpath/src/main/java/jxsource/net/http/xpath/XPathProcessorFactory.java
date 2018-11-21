package jxsource.net.http.xpath;

import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.ControllerManagerImpl;
import jxsource.net.proxy.DefaultSocketFactory;
import jxsource.net.proxy.delegate.Processor;
import jxsource.net.proxy.delegate.ProcessorFactory;
import jxsource.net.proxy.http.HttpProcessor;
import jxsource.net.proxy.http.entity.modifier.BufferedEntityDestinationSocketManager;
import jxsource.net.proxy.http.entity.modifier.HttpEntityModifyRequestHandler;

public class XPathProcessorFactory implements ProcessorFactory {
	
	private ControllerManager controllerManager = new ControllerManagerImpl<DefaultSocketFactory>(
			new DefaultSocketFactory());
	
	public Processor createProcessor() {
		// Each call gets a new Processor
		HttpProcessor processor = new HttpProcessor();
		HttpEntityModifyRequestHandler handler = new XPathRequestHandler(); 
		// Each handler has its EntityDestinationSocketManager instance
		handler.setEntityDestinationSocketManager(new BufferedEntityDestinationSocketManager());
		// But all handlers have the same ControllerManager
		handler.setControllerManager(controllerManager);
		handler.setEntityModifier(new XPathEntityModifier());
		processor.setRequestHandler(handler);
		return processor;
	}

}
