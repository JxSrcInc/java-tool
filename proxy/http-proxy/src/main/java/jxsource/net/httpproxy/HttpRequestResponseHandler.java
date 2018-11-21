package jxsource.net.httpproxy;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;


public abstract class HttpRequestResponseHandler {
	private RequestInfo requestInfo;
	private InputStream requestInputStream;
	private InputStream responseInputStream;
	private OutputStream responseOutputStream;
	
	public HttpResponse procRequest(RequestInfo requestInfo,
			InputStream requestInputStream, 
			InputStream responseInputStream,
			OutputStream responseOUtputStream) {
		// write request to responseOUtputStream
		// if request has content, read content from requestInputStream and write it to responseOutputStream
		// read response from responseInputStream
		return null;
	}
}
