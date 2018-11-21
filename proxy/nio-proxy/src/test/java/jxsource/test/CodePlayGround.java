package jxsource.test;

import java.io.ByteArrayInputStream;
import java.util.Random;

public class CodePlayGround {

	public static void main(String[] args) {
		CodePlayGround c = new CodePlayGround();
//		c.maxLongAndInt();
//		c.random();
//		c.thread();
//		c.inputStream();
	}

	public void inputStream() {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream("1234567890".getBytes());
			byte[] b = new byte[5];
			int i = 0;
			System.out.println(in.markSupported());
			i = in.read(b);
			System.out.println(new String(b,0,i));
			in.reset();
			while((i=in.read(b)) != -1) {
				System.out.print(new String(b,0,i));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void thread() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				for(int i=0; i<10; i++) {
					if(i == 5) {
						Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
					}
					System.out.println(Thread.currentThread().getPriority());
				}
			}
		});
		t.start();
	}
	public void maxLongAndInt() {
		long hexLong = 0x7FFFFFFFFFFFFFFFL;
		System.out.println(hexLong);
		int hexInt = 0x7FFFFFFF;
		System.out.println(hexInt);
		
	}
	
	public void random() {
		Random randomGenerator = new Random();
//		int sleepTime = randomGenerator.nextInt(100);
		for(int i=0; i<10; i++)
		System.out.println(randomGenerator.nextInt(1000));
	}
}
