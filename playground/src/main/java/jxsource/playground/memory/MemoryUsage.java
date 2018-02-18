package jxsource.playground.memory;

import jxsource.util.buffer.bytebuffer.ByteArray;

public class MemoryUsage {

	public static void main(String[] args) {
		int size = 1;
		boolean inc = true;
		for(int i=1; i<100; i++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(i%29 == 0) {
				inc = !inc;
				if(size == 0) size =1;
			}
			if(inc) {
				size = size*2;
			} else {
				size = size/2;
			}
			byte[] b = new byte[size];
			System.out.println(inc+", "+i+","+size+" , "+Integer.MAX_VALUE);
		}
	}
}
