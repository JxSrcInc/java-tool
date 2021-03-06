package jxsource.net.proxy.test.http;

import java.io.OutputStream;

import jxsource.net.proxy.app.TestHttpUtils;
import jxsource.net.proxy.http.HttpUtils;

import org.apache.http.HttpResponse;

public class ContentLengthEntityResponse implements EntityResponse{
	HttpUtils httpUtils = new HttpUtils();
	TestHttpUtils testUtils = new TestHttpUtils();
	HttpResponse header;
	byte[] body;
	public ContentLengthEntityResponse() {
		header = testUtils.createHttpResponse();
		String entity = "Content-Length message body.";
		header.addHeader("Content-Length", Integer.toString(entity.length()));
		body = entity.getBytes();
		System.out.println("create response with fixed content length: "+header);
		
	}
	public void write(OutputStream out) throws Exception {
		httpUtils.outputResponse(header, out);
		out.write(body);
		out.flush();
	}
}
