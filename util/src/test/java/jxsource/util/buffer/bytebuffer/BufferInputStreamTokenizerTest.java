package jxsource.util.buffer.bytebuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.buffer.bytebuffer.BufferInputStreamSrc;
import jxsource.util.buffer.bytebuffer.BufferInputStreamFilter;

import org.junit.Before;
import org.junit.Test;

public class BufferInputStreamTokenizerTest {
	ByteArrayInputStream in;
	byte[] src = "12345678901234567890".getBytes();
	BufferInputStreamTokenizer ibt;
	String token = "ABC";
	byte[] find = token.getBytes();
	int loopSize = 3;
	String s = "12345";
	String e = "67890";
	boolean withCheck;

	private byte[] init() {
		String src = "";
		for(int i=0; i<loopSize; i++) {
			src += s+token+e;
		}
		this.src = src.getBytes();
		in = new ByteArrayInputStream(this.src);
		ibt = new BufferInputStreamTokenizer(new BufferInputStreamSrc(in), find);
		return this.src;
	}
	private boolean equal(byte[] b1, byte[] b2) {
//		System.out.println(">"+new String(b1)+".");
//		System.out.println("<"+new String(b2)+".");
		return ByteArray.equal(b1,b2);
	}
	@Test
	public void lopp1Test() throws IOException {
		loopSize = 1;
		init();
		testWithToken();
	}
	@Test
	public void lopp3Test() throws IOException {
		init();
		testWithToken();
	}
	@Test
	public void lopp3WithCheckTest() throws IOException {
		withCheck = true;
		init();
		testWithToken();
	}
	@Test
	public void lopp1WithoutTokenTest() throws IOException {
		loopSize = 1;
		init();
		testWithoutToken();
	}
	@Test
	public void lopp3WithoutTokenTest() throws IOException {
		init();
		testWithoutToken();
	}
	@Test
	public void lopp3WithoutTokenWithCheckTest() throws IOException {
		withCheck = true;
		init();
		testWithoutToken();
	}
	private void testWithToken() throws IOException {
		for(int i=0; i<loopSize+2; i++) {
			boolean check = ibt.hasNext();
			if(i == 0) {
				assertTrue(equal(ibt.next(true),(s+token).getBytes()));
			} else 
			if(i == loopSize) {
				assertTrue(equal(ibt.next(true),e.getBytes()));
				
			} else
			if(i == loopSize+1 ) {
				assertTrue(ibt.next(true) == null);
				
			} else {
				assertTrue(equal(ibt.next(true),(e+s+token).getBytes()));
				
			}
		}
	}
	
	private void testWithoutToken() throws IOException {
		for(int i=0; i<loopSize+2; i++) {
			if(i == 0) {
				assertTrue(equal(ibt.next(),(s).getBytes()));
			} else 
			if(i == loopSize) {
				assertTrue(equal(ibt.next(),e.getBytes()));
				
			} else
			if(i == loopSize+1 ) {
				assertTrue(ibt.next() == null);
				
			} else {
				assertTrue(equal(ibt.next(),(e+s).getBytes()));
				
			}
		}
	}

/*	@Test
	public void smallSizeTest() throws IOException {
		byte[] buf = new byte[10];
		ByteArray ba = new ByteArray();
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, i);
		}
		assertTrue(ba.equals("12ABC6789012ABC67890".getBytes()));
	}

	@Test
	public void largeSizeTest() throws IOException {
		byte[] buf = new byte[40];
		ByteArray ba = new ByteArray();
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, i);
		}
		assertTrue(ba.equals("12ABC6789012ABC67890".getBytes()));
	}

	@Test
	public void largeReplaceTest() throws IOException {
		replace = "ASDFGG".getBytes();
		ibSrc.setReplace(replace);
		byte[] buf = new byte[8];
		ByteArray ba = new ByteArray();
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, i);
		}
		assertTrue(ba.equals("12ASDFGG6789012ASDFGG67890".getBytes()));
	}
	@Test
	public void largeBufferTest() throws IOException {
		long t = System.currentTimeMillis();
		int size = 1000000;
		byte[] temp = "1234567890".getBytes();
		ByteArray ba = new ByteArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				ba.append(temp);
			} else {
				ba.append("0000000000".getBytes());
			}
		}
		src = ba.getArray();
		init();
		int k = 0;
		byte[] buf = new byte[ba.getLimit()];
		ba = new ByteArray();
		while((k=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, k);
		}
		ByteArray r = new ByteArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				r.append("12ABC67890".getBytes());
			} else {
				r.append("0000000000".getBytes());
			}
		}
		assertTrue(ba.equals(r));
		System.out.println("used milli seconds: "+(System.currentTimeMillis()-t));
	}
	@Test
	public void largeBufferWithSmallSizeTest() throws IOException {
		long t = System.currentTimeMillis();
		int size = 100000;
		byte[] temp = "1234567890".getBytes();
		ByteArray ba = new ByteArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				ba.append(temp);
			} else {
				ba.append("0000000000".getBytes());
			}
		}
		src = ba.getArray();
		init();
		int k = 0;
		byte[] buf = new byte[ba.getLimit()/13];
		ba = new ByteArray();
		while((k=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, k);
		}
		ByteArray r = new ByteArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				r.append("12ABC67890".getBytes());
			} else {
				r.append("0000000000".getBytes());
			}
		}
		assertTrue(ba.equals(r));
		System.out.println("used milli seconds: "+(System.currentTimeMillis()-t));
	}

	@Test
	public void largeBufferWithSmallSizeSmallReplaceTest() throws IOException {
		long t = System.currentTimeMillis();
		int size = 100000;
		byte[] temp = "1234567890".getBytes();
		replace = "A".getBytes();
		ByteArray ba = new ByteArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				ba.append(temp);
			} else {
				ba.append("0000000000".getBytes());
			}
		}
		src = ba.getArray();
		init();
		int k = 0;
		byte[] buf = new byte[ba.getLimit()/13];
		ba = new ByteArray();
		while((k=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, k);
		}
		ByteArray r = new ByteArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				r.append("12A67890".getBytes());
			} else {
				r.append("0000000000".getBytes());
			}
		}
		assertTrue(ba.equals(r));
		System.out.println("used milli seconds: "+(System.currentTimeMillis()-t));
	}
	@Test
	public void largeBufferWithSmallSizeLargeReplaceTest() throws IOException {
		long t = System.currentTimeMillis();
		int size = 100000;
		byte[] temp = "1234567890".getBytes();
		replace = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
		ByteArray ba = new ByteArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				ba.append(temp);
			} else {
				ba.append("0000000000".getBytes());
			}
		}
		src = ba.getArray();
		init();
		int k = 0;
		byte[] buf = new byte[ba.getLimit()/13];
		ba = new ByteArray();
		while((k=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, k);
		}
		ByteArray r = new ByteArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				r.append("12ABCDEFGHIJKLMNOPQRSTUVWXYZ67890".getBytes());
			} else {
				r.append("0000000000".getBytes());
			}
		}
		assertTrue(ba.equals(r));
		System.out.println("used milli seconds: "+(System.currentTimeMillis()-t));
	}
*/
}
