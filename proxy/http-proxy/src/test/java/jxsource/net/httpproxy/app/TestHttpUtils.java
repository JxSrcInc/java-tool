package jxsource.net.httpproxy.app;

import java.text.SimpleDateFormat;
import java.util.Date;

import jxsource.net.httpproxy.UrlInfo;
import jxsource.net.httpproxy.HttpUtils;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;

public class TestHttpUtils {
	HttpUtils utils = new HttpUtils();
	SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ");
	
	public HttpRequest getHttpRequest(String uri) {
		return getHttpRequest("GET", uri);
	}

	public HttpRequest getHttpRequest(String method, String uri) {
		return new BasicHttpRequest(method, uri);
	}

	public HttpRequest createHttpRequest(String url) {
		return createHttpRequest("GET", url);
	}
	
	public HttpRequest createHttpRequest(String method, String url) {
		HttpRequest request = getHttpRequest(method, url);
		UrlInfo connInfo = utils.getUrlInfo(url);
		request.addHeader("Host", connInfo.getHostName());
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
