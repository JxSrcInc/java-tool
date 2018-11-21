package jxsource.test;

import org.apache.log4j.Logger;

public class SampleApp {
	private static Logger logger = Logger.getLogger(SampleApp.class);
	public void display() {
		if(logger.isDebugEnabled()) {
			logger.debug("debug message");
		} else {
			logger.warn("debug disabled");
		}		
	}
	public void run() {
		System.out.println("run start ....");
		display();
		System.out.println("run end.");
	}
	public static void main(String[] args) {
		new SampleApp().run();
	}
}
