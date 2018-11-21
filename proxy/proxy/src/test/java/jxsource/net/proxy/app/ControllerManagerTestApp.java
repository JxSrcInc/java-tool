package jxsource.net.proxy.app;


public class ControllerManagerTestApp{
	public static void main(String[] args) {
		ControllerManagerTester mgr = new ControllerManagerTester();
//		new Thread(new ControllerRunnable(mgr), "SCR-1").start();
//		new Thread(new ControllerRunnableModifier(mgr), "MCR-2").start();
//		new Thread(new ControllerRunnable(mgr), "SCR-3").start();
		for(int i=0; i<3; i++) {
			if(i == 2 || i == 4 || i == 6 || i == 8) {
				ControllerRunnableModifier crm = new ControllerRunnableModifier(mgr);
				crm.setLoopSize(500);
				new Thread(crm).start();				
			} else {
				ControllerRunnable crm = new ControllerRunnable(mgr);
				crm.setLoopSize(500);
				new Thread(crm).start();				
			}
			
		}
	}
}
