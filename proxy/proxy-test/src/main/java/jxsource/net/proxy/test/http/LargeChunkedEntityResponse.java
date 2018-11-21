package jxsource.net.proxy.test.http;

import java.io.OutputStream;
import java.util.Arrays;

import jxsource.net.proxy.app.ChunkUtils;
import jxsource.net.proxy.app.TestHttpUtils;
import jxsource.net.proxy.http.HttpUtils;
import jxsource.util.buffer.bytebuffer.ByteArray;

import org.apache.http.HttpResponse;

public class LargeChunkedEntityResponse implements EntityResponse{
	HttpUtils httpUtils = new HttpUtils();
	TestHttpUtils testUtils = new TestHttpUtils();
	HttpResponse header;
	
	public LargeChunkedEntityResponse() {
		header = testUtils.createHttpResponse();
		header.addHeader("Transfer-Encoding","chunked");
		System.out.println("create response with large chunked content: "+header);
	}

	public void write(OutputStream out) throws Exception {
		httpUtils.outputResponse(header, out);
		out.flush();
		
		ByteArray entity = new ByteArray();
		for(byte b=48; b<=57; b++) {
			byte[] data = new byte[999];
			Arrays.fill(data, b);
			entity.append(data);
			entity.append((byte)13);
		}
		for(byte b=65; b<=90; b++) {
			byte[] data = new byte[999];
			Arrays.fill(data, b);
			entity.append(data);
			entity.append((byte)13);
		}
		System.out.println(entity.length());
		entity = ChunkUtils.createChunk(1024*10*4, entity.getArray());
		out.write(entity.getArray());
		out.flush();
	}

}
