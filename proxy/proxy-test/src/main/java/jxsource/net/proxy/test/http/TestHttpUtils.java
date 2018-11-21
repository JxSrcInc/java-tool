package jxsource.net.proxy.test.http;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;

public class TestHttpUtils {
	SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ");
	
	public HttpRequest createHttpRequest(String protocol, String host) {
		return createHttpRequest("GET", protocol, host);
	}
	
	public HttpRequest createHttpRequest(String method, String protocol, String host) {
		String url = protocol+"://"+host;
		HttpRequest request = new BasicHttpRequest(method, url);
		request.addHeader("Host", host);
		request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,");
		request.addHeader("Accept-Encoding", "deflate");
		return request;
	}
	
	public HttpResponse createHttpResponse() {
		HttpResponse header = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK,"OK");
		header.addHeader("Content-Type", "text/html");
		header.addHeader("Server","HttpTestServer");
//		header.addHeader("Date"," Sat, 28 Feb 2015 19:14:27 GMT");
		header.addHeader("Date",format.format(new Date(System.currentTimeMillis()))+"GMT");
		return header;
	}

}
