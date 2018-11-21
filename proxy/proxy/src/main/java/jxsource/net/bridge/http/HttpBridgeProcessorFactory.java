package jxsource.net.bridge.http;

import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.ControllerManagerImpl;
import jxsource.net.proxy.DefaultSocketFactory;
import jxsource.net.proxy.delegate.Processor;
import jxsource.net.proxy.delegate.ProcessorFactory;
import jxsource.net.proxy.http.HttpProcessor;
import jxsource.net.proxy.http.entity.ProxyHttpEntityDestinationSocketManager;
import jxsource.net.proxy.http.entity.modifier.BufferedEntityDestinationSocketManager;

public class HttpBridgeProcessorFactory implements ProcessorFactory {
	
	private ControllerManager controllerManager = new ControllerManagerImpl<DefaultSocketFactory>(
			new DefaultSocketFactory());
	
	public Processor createProcessor() {
		// Each call gets a new Processor
		HttpProcessor processor = new HttpProcessor();
		HttpBridgeRequestHandler handler = new HttpBridgeRequestHandler(); 
		// Each handler has its EntityDestinationSocketManager instance
		handler.setEntityDestinationSocketManager(new BufferedEntityDestinationSocketManager());
		// But all handlers have the same ControllerManager
		handler.setControllerManager(controllerManager);
		handler.setEntityModifier(new HttpsBridgeEntityModifier());
		processor.setRequestHandler(handler);
		return processor;
	}
	
}
