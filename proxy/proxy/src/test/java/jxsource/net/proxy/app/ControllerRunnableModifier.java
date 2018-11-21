package jxsource.net.proxy.app;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import jxsource.net.proxy.Controller;
import jxsource.net.proxy.UrlInfo;

public class ControllerRunnableModifier extends ControllerRunnable {

	public ControllerRunnableModifier(ControllerManagerTester mgr) {
		super(mgr);
		logger = Logger.getLogger(ControllerRunnableModifier.class);
	}
	@Override
	public void run() {
		for(int i=0; i<loopSize; i++) {
			logger.debug("-------- loop "+i);
			if( i > 0 && i%2 == 0) {
				try {
					newSocket(new UrlInfo("http://localhost"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					transaction(new UrlInfo("http://localhost"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void newSocket(UrlInfo urlInfo) throws IOException {
		synchronized(mgr) {
			while(mgr.lock) {
				try {
					mgr.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mgr.lock = true;
		logger.debug("******* remove socket ");
		Controller controller = mgr.getControllerManager().getController(urlInfo);
		controller.removeSocket();
		logger.debug("******* create new socket ");
		Socket workingSocket = controller.getSocket();
		mgr.setWorkingSocket(workingSocket);
		logger.debug("******* new socket "+workingSocket.hashCode());
		controller.releaseSocket(workingSocket);
		mgr.lock = false;
		mgr.notifyAll();
		}
	}


}
