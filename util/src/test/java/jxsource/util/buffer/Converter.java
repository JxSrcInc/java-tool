package jxsource.util.buffer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jxsource.util.buffer.bytebuffer.ByteBufferProcessor;
import jxsource.util.buffer.bytebuffer.BufferInputStreamSrc;
import jxsource.util.buffer.bytebuffer.BufferOutputStreamDst;
import jxsource.util.buffer.bytebuffer.BufferOutputStreamFilter;

/*
 * Utility class to convert a bytebuffer java file to charbuffer java file.
 */
public class Converter extends ByteBufferProcessor {
	static String srcDir = "src/main/java/jxsource/util/buffer/byteBuffer";
	static String dstDir = "src/main/java/jxsource/util/buffer/charBuffer";
	public void convertByteArray(String byteFile, String charFile) throws IOException {
		BufferInputStreamSrc byteIn = new BufferInputStreamSrc(new FileInputStream(byteFile));
		BufferOutputStreamDst dstOut = new BufferOutputStreamDst(new FileOutputStream(charFile));
		BufferOutputStreamFilter charOut1 = new BufferOutputStreamFilter(dstOut, "byte".getBytes(),
				"char".getBytes());
		BufferOutputStreamFilter charOut = new BufferOutputStreamFilter(charOut1, "Byte".getBytes(),
				"Char".getBytes());
		run(byteIn, charOut);
	}
	public void convertStream(String byteFile, String charFile) throws IOException {
		BufferInputStreamSrc byteIn = new BufferInputStreamSrc(new FileInputStream(byteFile));
		BufferOutputStreamDst dstOut = new BufferOutputStreamDst(new FileOutputStream(charFile));
		BufferOutputStreamFilter charOut1 = new BufferOutputStreamFilter(dstOut, "byte".getBytes(),
				"char".getBytes());
		BufferOutputStreamFilter charOut = new BufferOutputStreamFilter(charOut1, "OutputStream".getBytes(),
				"Writer".getBytes());
		run(byteIn, charOut);
	}
	public static void main(String[] args) {
		Converter c = new Converter();
		try {
//			c.convertByteArray(srcDir+'/'+"ByteArray.java", dstDir+'/'+"CharArray.java");
			c.convertStream(srcDir+'/'+"BufferOutputStreamFilter.java", dstDir+'/'+"BufferWriterFilter.java");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
