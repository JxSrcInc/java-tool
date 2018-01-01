package jxsource.util.buffer.charbuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

import jxsource.util.buffer.charbuffer.*;

public class BufferReaderFilterTest {
	ByteArrayInputStream in;
	String src = "12345678901234567890";
	BufferReaderFilter ibSrc;
	char[] find = "345".toCharArray();
	char[] replace = "ABC".toCharArray();
	@Before
	public void init() {
		in = new ByteArrayInputStream(src.getBytes());
		ibSrc = new BufferReaderFilter(new BufferReaderSrc(new InputStreamReader(in)), find, replace);
	}
	@Test
	public void sameSizeTest() throws IOException {
		char[] buf = new char[20];
		CharArray ba = new CharArray();
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, i);
		}
		assertTrue(ba.equals("12ABC6789012ABC67890".toCharArray()));
	}
	@Test
	public void smallSizeTest() throws IOException {
		char[] buf = new char[10];
		CharArray ba = new CharArray();
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, i);
		}
		assertTrue(ba.equals("12ABC6789012ABC67890".toCharArray()));
	}

	@Test
	public void largeSizeTest() throws IOException {
		char[] buf = new char[40];
		CharArray ba = new CharArray();
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, i);
		}
		assertTrue(ba.equals("12ABC6789012ABC67890".toCharArray()));
	}

	@Test
	public void largeReplaceTest() throws IOException {
		replace = "ASDFGG".toCharArray();
		ibSrc.setReplace(replace);
		char[] buf = new char[8];
		CharArray ba = new CharArray();
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, i);
		}
		assertTrue(ba.equals("12ASDFGG6789012ASDFGG67890".toCharArray()));
	}
	@Test
	public void largeBufferTest() throws IOException {
		long t = System.currentTimeMillis();
		int size = 1000000;
		int skip = 1000;
		char[] temp = "1234567890".toCharArray();
		CharArray ba = new CharArray();
		for(int i=0; i<size; i++) {
			if(i%skip == 0) {
				ba.append(temp);
			} else {
				ba.append("0000000000".toCharArray());
			}
		}
		src = ba.toString();
		init();
		int k = 0;
		char[] buf = new char[ba.getLimit()];
		ba = new CharArray();
		while((k=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, k);
		}
		CharArray r = new CharArray();
		for(int i=0; i<size; i++) {
			if(i%skip == 0) {
				r.append("12ABC67890".toCharArray());
			} else {
				r.append("0000000000".toCharArray());
			}
		}
		assertTrue(ba.equals(r));
		System.out.println("used milli seconds: "+(System.currentTimeMillis()-t));
	}
	@Test
	public void largeBufferWithSmallSizeTest() throws IOException {
		long t = System.currentTimeMillis();
		int size = 100000;
		char[] temp = "1234567890".toCharArray();
		CharArray ba = new CharArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				ba.append(temp);
			} else {
				ba.append("0000000000".toCharArray());
			}
		}
		src = ba.toString();
		init();
		int k = 0;
		char[] buf = new char[ba.getLimit()/13];
		ba = new CharArray();
		while((k=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, k);
		}
		CharArray r = new CharArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				r.append("12ABC67890".toCharArray());
			} else {
				r.append("0000000000".toCharArray());
			}
		}
		assertTrue(ba.equals(r));
		System.out.println("used milli seconds: "+(System.currentTimeMillis()-t));
	}

	@Test
	public void largeBufferWithSmallSizeSmallReplaceTest() throws IOException {
		long t = System.currentTimeMillis();
		int size = 100000;
		char[] temp = "1234567890".toCharArray();
		replace = "A".toCharArray();
		CharArray ba = new CharArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				ba.append(temp);
			} else {
				ba.append("0000000000".toCharArray());
			}
		}
		src = ba.toString();
		init();
		int k = 0;
		char[] buf = new char[ba.getLimit()/13];
		ba = new CharArray();
		while((k=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, k);
		}
		CharArray r = new CharArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				r.append("12A67890".toCharArray());
			} else {
				r.append("0000000000".toCharArray());
			}
		}
		assertTrue(ba.equals(r));
		System.out.println("used milli seconds: "+(System.currentTimeMillis()-t));
	}
	@Test
	public void largeBufferWithSmallSizeLargeReplaceTest() throws IOException {
		long t = System.currentTimeMillis();
		int size = 100000;
		char[] temp = "1234567890".toCharArray();
		replace = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		CharArray ba = new CharArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				ba.append(temp);
			} else {
				ba.append("0000000000".toCharArray());
			}
		}
		src = ba.toString();
		init();
		int k = 0;
		char[] buf = new char[ba.getLimit()/13];
		ba = new CharArray();
		while((k=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, k);
		}
		CharArray r = new CharArray();
		for(int i=0; i<size; i++) {
			if(i%1000 == 0) {
				r.append("12ABCDEFGHIJKLMNOPQRSTUVWXYZ67890".toCharArray());
			} else {
				r.append("0000000000".toCharArray());
			}
		}
		assertTrue(ba.equals(r));
		System.out.println("used milli seconds: "+(System.currentTimeMillis()-t));
	}

}
