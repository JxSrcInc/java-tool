package jxsource.util.buffer.charbuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import jxsource.util.io.FileUtil;

import org.junit.Before;
import org.junit.Test;

public class BufferReaderSrcTest {
	ByteArrayInputStream in;
	String src = "12345678901234567890";
	BufferReaderSrc ibSrc;
	
	@Before
	public void init() {
		in = new ByteArrayInputStream(src.getBytes());
		ibSrc = new BufferReaderSrc(new InputStreamReader(in));
	}
	@Test
	public void oneReadTest() throws IOException {
		char[] buf = new char[20];
		int i = ibSrc.read(buf);
//		System.out.println(new String(buf));
		assertTrue(CharArray.equal(buf, src.toCharArray()));
	}
	@Test
	public void threeReadTest() throws IOException {
		CharArray r = new CharArray();
		char[] buf = new char[6];
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			r.append(buf,0,i);
		}
		assertTrue(r.equals(src));
	}
	static String srcDir = "src/main/java/jxsource/util/buffer/charbuffer";
	@Test
	public void fileTest() throws IOException {
		String file = srcDir+"/CharArray.java";
		ibSrc = new BufferReaderSrc(new InputStreamReader(new FileInputStream(file)));
		CharArray r = new CharArray();
		char[] buf = new char[6];
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			r.append(buf,0,i);
		}
		char[] src = FileUtil.loadCharArray(file);
		assertTrue(r.equals(src));
	}
	
}
