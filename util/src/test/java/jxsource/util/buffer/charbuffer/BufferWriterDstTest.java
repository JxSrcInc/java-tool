package jxsource.util.buffer.charbuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.buffer.bytebuffer.BufferOutputStreamDst;
import jxsource.util.io.FileUtil;

import org.junit.Before;
import org.junit.Test;

public class BufferWriterDstTest {

	ByteArrayOutputStream out;
	String src = "12345678901234567890";
	BufferWriterDst obos;
	
	@Before
	public void init() {
		out = new ByteArrayOutputStream();
		obos = new BufferWriterDst(new OutputStreamWriter(out));
	}
	@Test
	public void oneWriteTest() throws IOException {
		obos.write(src);
		obos.close();
		assertTrue(out.toString().equals(src));
	}
	public void twoWriteTest() throws IOException {
		obos.write(src);
		obos.write(src);
		obos.close();
		CharArray ba = new CharArray();
		ba.append(src);
		ba.append(src);
		assertTrue(out.toString().equals(ba.toString()));
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
