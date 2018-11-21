package jxsource.net.httpproxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxsource.net.httpproxy.UrlInfo;
import jxsource.net.httpproxy.exception.MessageHeaderException;
import jxsource.net.httpproxy.io.HttpInputStream;
import jxsource.util.buffer.bytebuffer.ByteArray;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.DefaultHttpRequestWriter;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.DefaultHttpResponseWriter;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.impl.io.SessionOutputBufferImpl;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicRequestLine;

public class HttpUtils {
	private final byte[] EndHttpRequest = new byte[] { 13, 10, 13, 10 };
	private SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ");

	public UrlInfo getUrlInfo(HttpRequest request) {
		return getUrlInfo(request.getRequestLine().getUri());
	}

	public UrlInfo getUrlInfo(String url) {
		return new UrlInfo(url);
	}

	public boolean isEntityRequest(HttpRequest request) {
		String method = request.getRequestLine().getMethod().toLowerCase();
		if (!method.equals("post") && !method.equals("get")) {
			System.err.println("Unsupport HTTP method !!!!");
		}
		return (isEntityMessage(request) || method.equals("post"));
	}

	public boolean isEntityMessage(HttpMessage messageHead) {
		Header contentLength = messageHead.getFirstHeader("Content-Length");
		Header transferEncoding = messageHead
				.getFirstHeader("Transfer-Encoding");
		return (contentLength != null || (transferEncoding != null && transferEncoding
				.getValue().equals("chunked")));
	}
	public ByteArray getMessageHead(InputStream in) throws IOException {
		if(in instanceof HttpInputStream) {
			HttpInputStream httpInputStream = (HttpInputStream) in;
			return httpInputStream.getHttpHeader();
		} else {
//			System.err.println("InputStream is not HttpInputStream.");
			return _getMessageHead(in);
		}
	}
	// this is a backup. should not call
	public ByteArray _getMessageHead(InputStream in) throws IOException {
		byte[] buffer = new byte[1];
		ByteArray byteArray = new ByteArray();
		// read one byte
		try {
			while (in.read(buffer) != -1) {
				byteArray.append(buffer[0]);
				if (byteArray.length() < 4) {
					continue;
				}
				int offset = byteArray.length() - 4;
				byte[] last4bytes = byteArray.subArray(offset);
				boolean endHttpRequest = true;
				for (int k = 0; k < 4; k++) {
					if (last4bytes[k] != EndHttpRequest[k]) {
						endHttpRequest = false;
						break;
					}
				}
				if (endHttpRequest) {
					break;
				}
			}
		} catch (Exception e) {
			throw new MessageHeaderException(byteArray.length()
					+ " bytes read.", e);
		}
		if(byteArray.length() == 0) {
			throw new MessageHeaderException("Empty message header. -> in.read() returns -1 at the first call -> InputStream close.");
		} else {
			return byteArray;
		}
	}

	public HttpRequest getHttpRequest(InputStream in) throws IOException,
			HttpException {
		ByteArray byteArray = getMessageHead(in);
		return getHttpRequest(byteArray);
	}

	public RequestInfo createRequestInfo(InputStream in)
			throws IOException, HttpException {
		HttpRequest src = getHttpRequest(in);
		RequestLine srcRequestLine = src.getRequestLine();
		String method = srcRequestLine.getMethod();
		ProtocolVersion protocolVersion = srcRequestLine.getProtocolVersion();
		String uri = srcRequestLine.getUri();
		UrlInfo urlInfo = new UrlInfo(uri);
		if(urlInfo.getHostname() == null ) {
			String host = src.getFirstHeader("Host").getValue();
			urlInfo.setHost(host);
		}
		String path = urlInfo.getPath();
		RequestLine requestLine = new BasicRequestLine(method, path,
				protocolVersion);
		HttpRequest request = new DefaultHttpRequestFactory()
				.newHttpRequest(requestLine);
		for (Header h : src.getAllHeaders()) {
			if (h.getName().toLowerCase().equals("proxy-connection")) {
				continue;
			} else {
				request.addHeader(h);
			}
		}
		if (request.getFirstHeader("Host") == null) {
			System.err.println("Add Host: " + urlInfo.getHost());
			request.addHeader("Host", urlInfo.getHost());
		}
		RequestInfo remoteRequest = new RequestInfo();
		remoteRequest.setRequest(request);
		remoteRequest.setUrlInfo(urlInfo);
		return remoteRequest;
	}

	public HttpRequest getHttpRequest(ByteArray byteArray) throws IOException,
			HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionInputBufferImpl inbuffer = new SessionInputBufferImpl(metrics,
				byteArray.length());
		ByteArrayInputStream in = new ByteArrayInputStream(byteArray.getArray());
		inbuffer.bind(in);
		DefaultHttpRequestParser parser = new DefaultHttpRequestParser(inbuffer);
		HttpRequest request = parser.parse();
		return request;
	}

	public HttpResponse getHttpResponse(InputStream in) throws IOException,
			HttpException {
		ByteArray byteArray = getMessageHead(in);
		return getHttpResponse(byteArray);
	}

	public HttpResponse getHttpResponse(ByteArray byteArray)
			throws IOException, HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionInputBufferImpl inbuffer = new SessionInputBufferImpl(metrics,
				byteArray.length());
		ByteArrayInputStream in = new ByteArrayInputStream(byteArray.getArray());
		inbuffer.bind(in);
		DefaultHttpResponseParser parser = new DefaultHttpResponseParser(
				inbuffer);
		HttpResponse response = parser.parse();
		return response;
	}

	public void outputRequest(HttpRequest request, OutputStream out)
			throws IOException, HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionOutputBufferImpl outbuffer = new SessionOutputBufferImpl(
				metrics, Constants.SessionBufferSize);
		DefaultHttpRequestWriter writer = new DefaultHttpRequestWriter(
				outbuffer);
		outbuffer.bind(out);
		writer.write(request);
		outbuffer.flush();
	}

	public String printRequest(HttpRequest request) {
		try {
			return new String(getRequestBytes(request));
		} catch (Exception e) {
			return "" + request;
		}
	}

	public byte[] getRequestBytes(HttpRequest request) throws IOException,
			HttpException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		outputRequest(request, out);
		out.close();
		return out.toByteArray();
	}

	public void outputResponse(HttpResponse response, OutputStream out)
			throws IOException, HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionOutputBufferImpl outbuffer = new SessionOutputBufferImpl(
				metrics, Constants.SessionBufferSize);
		DefaultHttpResponseWriter writer = new DefaultHttpResponseWriter(
				outbuffer);
		outbuffer.bind(out);
		writer.write(response);
		outbuffer.flush();
	}

	public String printResponse(HttpResponse response) {
		try {
			return new String(getResponseBytes(response));
		} catch (Exception e) {
			return "" + response;
		}
	}

	public byte[] getResponseBytes(HttpResponse response) throws IOException,
			HttpException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		outputResponse(response, out);
		out.close();
		return out.toByteArray();
	}
	
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
		UrlInfo connInfo = getUrlInfo(url);
		request.addHeader("Host", connInfo.getHostName());
		request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,");
		request.addHeader("Accept-Encoding", "deflate");
		return request;
	}
	
	public HttpResponse createHttpResponse() {
		HttpResponse header = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK,"OK");
		header.addHeader("Content-Type", "text/html");
		header.addHeader("Server","HttpTestServer");
		header.addHeader("Date",format.format(new Date(System.currentTimeMillis()))+"GMT");
		return header;
	}
	

/*
	public  ContentType getContentType(HttpResponse response) {
		ContentType mimeType = new ContentType();
		for (Header header : response.getHeaders("Content-Type")) {
			String value = header.getValue().trim();
			int indexCharset = 0;
			if ((indexCharset = value.toLowerCase().indexOf("charset=")) >= 0) {
				mimeType.setCharset(value.substring(indexCharset + 8));
			} 
			int index = 0;
			int indexSemiColon = value.toLowerCase().indexOf(';');
			if ((index = value.toLowerCase().indexOf('/')) > 0) {
				mimeType.setType(value.substring(0, index));
				if(indexSemiColon != -1) {
					mimeType.setSubType(value.substring(index + 1, indexSemiColon));
				} else {
					mimeType.setSubType(value.substring(index + 1));					
				}
			}
		}
		return mimeType;
	}
*/
}
