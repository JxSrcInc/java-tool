package jxsource.net.proxy.test.http;

import java.io.OutputStream;

import jxsource.net.proxy.app.ChunkUtils;
import jxsource.net.proxy.app.TestHttpUtils;
import jxsource.net.proxy.http.HttpUtils;
import jxsource.util.buffer.bytebuffer.ByteArray;

import org.apache.http.HttpResponse;

public class ChunkedEntityResponse implements EntityResponse{
	HttpUtils httpUtils = new HttpUtils();
	TestHttpUtils testUtils = new TestHttpUtils();
	HttpResponse header;
	
	public ChunkedEntityResponse() {
		header = testUtils.createHttpResponse();
		header.addHeader("Transfer-Encoding","chunked");
		System.out.println("create response with chunked content: "+header);
	}

	public void write(OutputStream out) throws Exception {
		httpUtils.outputResponse(header, out);
		out.flush();
		String entityStr = "This is chunked entity message with chunk size unknown.";
		ByteArray entity = ChunkUtils.createChunk(25, entityStr.getBytes());
		System.out.println("entity length: "+entity.length());
		out.write(entity.getArray());
		out.flush();
	}

}
