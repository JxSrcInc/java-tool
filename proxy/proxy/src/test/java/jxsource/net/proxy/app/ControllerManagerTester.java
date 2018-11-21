package jxsource.net.proxy.app;

import java.net.Socket;
import java.util.Random;

import jxsource.net.proxy.Controller;
import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.ControllerManagerImpl;
import jxsource.net.proxy.DefaultSocketFactory;

import org.apache.log4j.Logger;

public class ControllerManagerTester {
	private static Logger logger = Logger.getLogger(ControllerManagerTester.class);
	private volatile ControllerManager controllerManager
		= new ControllerManagerImpl<DefaultSocketFactory>(new DefaultSocketFactory());
	private volatile Controller workingController;
	private volatile Socket workingSocket;
	public volatile boolean lock;
	Random randomGenerator = new Random();

	public synchronized ControllerManager getControllerManager() {
		return controllerManager;
	}

	public synchronized void setControllerManager(
			ControllerManager channelManager) {
		this.controllerManager = channelManager;
	}

	public synchronized Controller getWorkingController() {
		return workingController;
	}

	public synchronized void setWorkingController(Controller workingController) {
		this.workingController = workingController;
	}

	public synchronized Socket getWorkingSocket() {
		return workingSocket;
	}

	public synchronized void setWorkingSocket(Socket workingSocket) {
		this.workingSocket = workingSocket;
	}

	public void sleep() {
		try {
			int sleepTime = randomGenerator.nextInt(10);
			logger.debug("sleep "+sleepTime);
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	
	}

}
