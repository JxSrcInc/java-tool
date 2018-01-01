package jxsource.util.buffer.charbuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.buffer.bytebuffer.BufferInputStreamSrc;
import jxsource.util.buffer.bytebuffer.BufferInputStreamFilter;

import org.junit.Before;
import org.junit.Test;

public class BufferReaderTokenizerTest {
	ByteArrayInputStream in;
	String src = "12345678901234567890";
	BufferReaderTokenizer ibt;
	String token = "ABC";
	char[] find = token.toCharArray();
	int loopSize = 3;
	String s = "12345";
	String e = "67890";
	boolean withCheck;

	private String init() {
		String src = "";
		for(int i=0; i<loopSize; i++) {
			src += s+token+e;
		}
		this.src = src;
		in = new ByteArrayInputStream(this.src.getBytes());
		ibt = new BufferReaderTokenizer(new BufferReaderSrc(new InputStreamReader(in)), find);
		return this.src;
	}
	private boolean equal(char[] b1, char[] b2) {
//		System.out.println(">"+new String(b1)+".");
//		System.out.println("<"+new String(b2)+".");
		return CharArray.equal(b1,b2);
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
				assertTrue(equal(ibt.next(true),(s+token).toCharArray()));
			} else 
			if(i == loopSize) {
				assertTrue(equal(ibt.next(true),e.toCharArray()));
				
			} else
			if(i == loopSize+1 ) {
				assertTrue(ibt.next(true) == null);
				
			} else {
				assertTrue(equal(ibt.next(true),(e+s+token).toCharArray()));
				
			}
		}
	}
	
	private void testWithoutToken() throws IOException {
		for(int i=0; i<loopSize+2; i++) {
			if(i == 0) {
				assertTrue(equal(ibt.next(),(s).toCharArray()));
			} else 
			if(i == loopSize) {
				assertTrue(equal(ibt.next(),e.toCharArray()));
				
			} else
			if(i == loopSize+1 ) {
				assertTrue(ibt.next() == null);
				
			} else {
				assertTrue(equal(ibt.next(),(e+s).toCharArray()));
				
			}
		}
	}

}
