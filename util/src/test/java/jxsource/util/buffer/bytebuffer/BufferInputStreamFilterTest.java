package jxsource.util.buffer.bytebuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

import jxsource.util.buffer.charbuffer.*;

public class BufferInputStreamFilterTest {
	ByteArrayInputStream in;
	byte[] src = "12345678901234567890".getBytes();
	BufferInputStreamFilter ibSrc;
	byte[] find = "345".getBytes();
	byte[] replace = "ABC".getBytes();
	@Before
	public void init() {
		in = new ByteArrayInputStream(src);
		ibSrc = new BufferInputStreamFilter(new BufferInputStreamSrc(in), find, replace);
	}
	@Test
	public void sameSizeTest() throws IOException {
		byte[] buf = new byte[20];
		ByteArray ba = new ByteArray();
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			ba.append(buf, 0, i);
		}
		assertTrue(ba.equals("12ABC6789012ABC67890".getBytes()));
	}
	@Test
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
		int skip = 1000;
		byte[] temp = "1234567890".getBytes();
		ByteArray ba = new ByteArray();
		for(int i=0; i<size; i++) {
			if(i%skip == 0) {
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
			if(i%skip == 0) {
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

}
