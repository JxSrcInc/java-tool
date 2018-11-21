package jxsource.net.proxy.http;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jxsource.net.proxy.UrlInfo;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.junit.Before;
import org.junit.Test;

public class HeadUtilsTest {
	HttpUtils httpUtils = new HttpUtils();
	HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	ByteArrayInputStream in;
	String requestSrc;
	@Before
	public void init() {
		requestSrc = httpUtils.printRequest(httpUtils.createHttpRequest("http://localhost"));
		in = new ByteArrayInputStream(requestSrc.getBytes());
	}
	
	@Test
	public void test() throws IOException, HttpException {
		HttpRequest request = httpUtils.getHttpRequest(in);
		assertTrue(httpUtils.printRequest(request).equals(requestSrc));
	}
	@Test
	public void replaceTest() {
		HttpRequest request = httpUtils.getHttpRequest("GET", "http://www.fidelity.com");
		request.addHeader("Host", "bridge__www.fidelity.com");
		request.addHeader("Accept", "text/html,bridge__www.fidelity.com/xhtml+xml,application/bridge__xml;q=0.9,*/*;q=0.8,");
		request.addHeader("Accept-Encoding", "deflate");
		headerUtils.setHttpMessage(request);
		assertTrue(headerUtils.replaceAllHeaders("bridge__", "").toString().indexOf("bridge__") == -1);

	}
}
