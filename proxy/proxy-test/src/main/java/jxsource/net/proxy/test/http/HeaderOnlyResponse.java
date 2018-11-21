package jxsource.net.proxy.test.http;

import java.io.OutputStream;

import jxsource.net.proxy.app.TestHttpUtils;
import jxsource.net.proxy.http.HttpUtils;

import org.apache.http.HttpResponse;

public class HeaderOnlyResponse implements EntityResponse{
	HttpUtils httpUtils = new HttpUtils();
	TestHttpUtils testUtils = new TestHttpUtils();
	HttpResponse header;
	byte[] body;
	public HeaderOnlyResponse() {
		header = testUtils.createHttpResponse();
		System.out.println("create response with header only: "+header);
		
	}
	public void write(OutputStream out) throws Exception {
		httpUtils.outputResponse(header, out);
		out.write(new byte[0]);
		out.flush();
	}
}
