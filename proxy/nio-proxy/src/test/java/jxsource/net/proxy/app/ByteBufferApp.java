package jxsource.net.proxy.app;

import java.nio.ByteBuffer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ByteBufferApp 
{
	ByteBuffer buf = ByteBuffer.allocate(1024);

	private void printInfo() {
		printInfo(buf);
	}
	private void printInfo(ByteBuffer buf) {
		System.out.println("limit="+buf.limit());
		System.out.println("position="+buf.position());
		System.out.println("array.offset="+buf.arrayOffset());
		System.out.println("-------------------");
	}
	public void go() {
		printInfo();
		byte[] src = "1234567890".getBytes();
		buf.put(src);
		buf.flip();
		byte[] dst = new byte[5];
		for(int i=0; i<2; i++) {
			ByteBuffer bb = buf.get(dst);
			System.out.println(""+i+","+new String(dst));
			printInfo(bb);
		}
		buf.flip();
		System.out.println("*** flip");
		printInfo();
		buf.clear();
		System.out.println("*** clear");
		printInfo();
		src = "ABCDE".getBytes();
		buf.put(src);
		buf.flip();
		for(int i=0; i<3; i++) {
			ByteBuffer bb = buf.get(dst);
			System.out.println(""+i+","+new String(dst));
			printInfo(bb);
			if(i==0)buf.rewind();
			if(i==1)buf.flip();
		}
		
	}
	public static void main(String[] args) {
		new ByteBufferApp().go();
	}
}
