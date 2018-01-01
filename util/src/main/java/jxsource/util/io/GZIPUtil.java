package jxsource.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import jxsource.util.buffer.bytebuffer.ByteArray;

public class GZIPUtil {
	public byte[] ungzip(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		return ungzip(bais);
	}

	public byte[] ungzip(byte[] bytes, int offset, int length) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes, offset, length);
		return ungzip(bais);
	}

	public byte[] ungzip(InputStream in) throws IOException {
		byte[] buf = new byte[1024*10*4];
		GZIPInputStream gis = new GZIPInputStream(in);
		ByteArray ba = new ByteArray();
		int i = 0;
		while((i=gis.read(buf)) != -1) {
			ba.append(buf,0,i);
		}
		return ba.getArray();
	}
	public void gzip(OutputStream out, byte[] src) throws IOException {
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(src, 0, src.length);
        gzip.finish();
	}
	public byte[] gzip(byte[] src) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        gzip(out, src);
        out.close();
        return out.toByteArray();
	}

}
