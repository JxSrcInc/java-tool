package jxsource.net.httpproxy;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import jxsource.net.httpproxy.UrlInfo;

public class ResponseInfo {
	HttpResponse response;
	public ResponseInfo(HttpResponse response) {
		this.response = response;
	}
	public HttpResponse getResponse() {
		return response;
	}
	public boolean isClose() {
		return false;
	}
	public int getStatusCode() {
		return response.getStatusLine().getStatusCode();
	}
	public boolean hasContent() {
		return false;
	}
}
