package jxsource.net.httpproxy;

import org.apache.http.HttpRequest;

import jxsource.net.httpproxy.UrlInfo;

public class RequestInfo {
	HttpRequest request;
	UrlInfo urlInfo;
	public HttpRequest getRequest() {
		return request;
	}
	public void setRequest(HttpRequest request) {
		this.request = request;
	}
	public UrlInfo getUrlInfo() {
		return urlInfo;
	}
	public void setUrlInfo(UrlInfo urlInfo) {
		this.urlInfo = urlInfo;
	}
}
