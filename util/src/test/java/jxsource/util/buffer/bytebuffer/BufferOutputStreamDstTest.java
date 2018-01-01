package jxsource.util.buffer.bytebuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.buffer.bytebuffer.BufferOutputStreamDst;
import jxsource.util.io.FileUtil;

import org.junit.Before;
import org.junit.Test;

public class BufferOutputStreamDstTest {

	ByteArrayOutputStream out;
	byte[] src = "12345678901234567890".getBytes();
	BufferOutputStreamDst obos;
	
	@Before
	public void init() {
		out = new ByteArrayOutputStream();
		obos = new BufferOutputStreamDst(out);
	}
	@Test
	public void oneWriteTest() throws IOException {
		obos.write(src);
		obos.close();
		assertTrue(ByteArray.equal(out.toByteArray(), src));
	}
	public void twoWriteTest() throws IOException {
		obos.write(src);
		obos.write(src);
		obos.close();
		ByteArray ba = new ByteArray();
		ba.append(src);
		ba.append(src);
		assertTrue(ByteArray.equal(out.toByteArray(), ba.getArray()));
	}
	static String srcDir = "src/main/java/jxsource/util/buffer/bytebuffer";
	@Test
	public void fileTest() throws IOException {
		String file = srcDir+"/ByteArray.java";
		byte[] src = FileUtil.loadByteArray(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferOutputStreamDst obDst = new BufferOutputStreamDst(out);
		int len = 0;
		int size = 100;
		int i = 0;
		while(true) {
			if(len == src.length) {
				break;
			}
			int outSize = Math.min(src.length-len, size);
			obDst.write(src, len, outSize);
			obDst.flush();
			len += outSize;
		}
		obDst.close();
		assertTrue(src.length == out.size());
	}
	
}
