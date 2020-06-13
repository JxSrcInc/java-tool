package jxsource.playground.thread;

public class RunnableImpl implements Runnable{

	public void run() {
		print();
	}
	public static void print() {
	    String threadName = Thread.currentThread().getName();
		System.out.println("*** Runnable: "+threadName);
	}

}
