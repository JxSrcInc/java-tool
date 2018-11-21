package jxsource.net.httpproxy;

public class T {

	static boolean b = false;
	
	class R implements Runnable {
		public R() {}
		public void run() {
			if(b) {
				System.out.println("true");
			} else {
				System.out.println("false");
			}
		}
	}

	public static void main(String[] arge) {
		Runnable r = new T().new R();
		Thread t = new Thread(r);
		t.start();
		b = true;
	}
}
