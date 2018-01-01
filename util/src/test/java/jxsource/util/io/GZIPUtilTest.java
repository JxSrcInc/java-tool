package jxsource.util.io;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jxsource.util.buffer.bytebuffer.ByteArray;

import org.junit.Test;

public class GZIPUtilTest {

	GZIPUtil util = new GZIPUtil();
	
	@Test
	public void test() throws IOException {
		byte[] src = "1234567890".getBytes();
		byte[] gzip = util.gzip(src);
		System.out.println(gzip.length);
		byte[] unzip = util.ungzip(gzip);
		byte[] dst = util.gzip(unzip);
		assertTrue(ByteArray.equal(src, unzip));
		assertTrue(ByteArray.equal(gzip, dst));
	}
}
