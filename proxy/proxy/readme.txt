Java main method: ProxyServer.setServerFactory() then start in a thread

ProxyServer listens on a server port, accept a call and pass it to WorkThread.

WorkThread:
				RequestInfo remoteInfo = httpUtils.createRequestInfo(localSocket.getInputStream());
				logger.debug(remoteInfo.getRequest());
				processor = delegate.getProcessor(remoteInfo.getUrlInfo());
				// each Processer has its RequestHandler 
				// but RequestHandler will share a common ControllerManager
				// which may be a global instance or thread based instance.
				processor.getRequestHandler().setLocalSocket(localSocket);
				processor.handleRequest(remoteInfo);

Processor handles one HTTP communication between local client and remote server

		requestHandler.setRemoteInfo(remoteInfo);
		Socket remoteSocket = requestHandler.getRemoteSocket();
		if(!remoteSocket.isConnected()) {
			remoteSocket.connect();
		}	
		executorState = requestHandler.procRemoteRequest();
		executorState = requestHandler.procRemoteResponse();

RequestHandler does the real work because http and https use different connections and 
simple pass, xpath and bridge (modify https content) require different processing for
remote connection and response handling.

	
------------------------

ServerFactory
	SocketFactory
	Delegate: set ProcessorFactory in Delegate
		HttpProcessorFactory: set ControllerManager and RequestHandler
		Executer?
