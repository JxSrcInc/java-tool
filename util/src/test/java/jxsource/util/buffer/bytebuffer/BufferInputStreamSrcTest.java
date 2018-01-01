package jxsource.util.buffer.bytebuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.buffer.bytebuffer.BufferInputStreamSrc;
import jxsource.util.io.FileUtil;

import org.junit.Before;
import org.junit.Test;

public class BufferInputStreamSrcTest {
	ByteArrayInputStream in;
	byte[] src = "12345678901234567890".getBytes();
	BufferInputStreamSrc ibSrc;
	
	@Before
	public void init() {
		in = new ByteArrayInputStream(src);
		ibSrc = new BufferInputStreamSrc(in);
	}
	@Test
	public void oneReadTest() throws IOException {
		byte[] buf = new byte[20];
		int i = ibSrc.read(buf);
//		System.out.println(new String(buf));
		assertTrue(ByteArray.equal(buf, src));
	}
	@Test
	public void threeReadTest() throws IOException {
		ByteArray r = new ByteArray();
		byte[] buf = new byte[6];
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			r.append(buf,0,i);
		}
		assertTrue(r.equals(src));
	}
	static String srcDir = "src/main/java/jxsource/util/buffer/bytebuffer";
	@Test
	public void fileTest() throws IOException {
		String file = srcDir+"/ByteArray.java";
		ibSrc = new BufferInputStreamSrc(new FileInputStream(file));
		ByteArray r = new ByteArray();
		byte[] buf = new byte[6];
		int i = 0;
		while((i=ibSrc.read(buf)) != -1) {
			r.append(buf,0,i);
		}
		byte[] src = FileUtil.loadByteArray(file);
		assertTrue(r.equals(src));
	}
	
}
