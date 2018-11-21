package jxsource.net.httpsproxy.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxsource.net.proxy.UrlInfo;
import jxsource.util.buffer.bytebuffer.ByteArray;

import org.apache.http.Header;
import org.apache.http.HttpException;
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
	private static final byte[] EndHttpRequest = new byte[] { 13, 10, 13, 10 };
	public static final SimpleDateFormat httpDateformat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ");
	private static SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ");
	int SessionBufferSize = 1024*8;
	
	public static final String getHttpFormatDate() {
		return httpDateformat.format(new Date(System.currentTimeMillis()))+"GMT";
	}
	public static HttpResponse getHttpResponse(int status, String message) {
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, status, message);
		response.addHeader("Server","JxSource HttpProxy Server");
		response.addHeader("Date",getHttpFormatDate());
		return response;
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

	public void writeRequest(HttpRequest request, SocketChannel channel)
			throws IOException, HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionOutputBufferImpl outbuffer = new SessionOutputBufferImpl(
				metrics, SessionBufferSize);
		DefaultHttpRequestWriter writer = new DefaultHttpRequestWriter(
				outbuffer);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		outbuffer.bind(out);
		writer.write(request);
		outbuffer.flush();
		byte[] data = out.toByteArray();
		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
		byteBuffer.put(data);
		byteBuffer.flip();
		channel.write(byteBuffer);
		byteBuffer.clear();

	}
	public void outputResponse(HttpResponse response, OutputStream out) throws IOException, HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionOutputBufferImpl outbuffer = new SessionOutputBufferImpl(metrics, SessionBufferSize);
		DefaultHttpResponseWriter writer = new DefaultHttpResponseWriter(outbuffer);
		outbuffer.bind(out);
		writer.write(response);
		outbuffer.flush();
	}
	
	public String printResponse(HttpResponse response) {
		try {
			return new String(getResponseBytes(response));
		} catch(Exception e) {
			return ""+response;
		}
	}
	public byte[] getResponseBytes(HttpResponse response) throws IOException, HttpException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		outputResponse(response, out);
		out.close();
		return out.toByteArray();
	}
	public String printRequest(HttpRequest request) {
		try {
			return new String(getRequestBytes(request));
		} catch(Exception e) {
			return ""+request;
		}
	}
	public byte[] getRequestBytes(HttpRequest request) throws IOException, HttpException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		outputRequest(request, out);
		out.close();
		return out.toByteArray();
	}
	public void outputRequest(HttpRequest request, OutputStream out) throws IOException, HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionOutputBufferImpl outbuffer = new SessionOutputBufferImpl(metrics, SessionBufferSize);
		DefaultHttpRequestWriter writer = new DefaultHttpRequestWriter(outbuffer);
		outbuffer.bind(out);
		writer.write(request);
		outbuffer.flush();
	}

	public static HttpRequest createHttpRequest(String url) {
		return createHttpRequest("GET", url);
	}
	
	public static HttpRequest createHttpRequest(String method, String url) {
		HttpRequest request = new BasicHttpRequest(method, url);
		UrlInfo connInfo = new UrlInfo(url);
		request.addHeader("Host", connInfo.getHostName());
		request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,");
		request.addHeader("Accept-Encoding", "deflate");
		return request;
	}
	
	public static HttpResponse createHttpResponse() {
		HttpResponse header = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK,"OK");
		header.addHeader("Content-Type", "text/html");
		header.addHeader("Server","HttpTestServer");
//		header.addHeader("Date"," Sat, 28 Feb 2015 19:14:27 GMT");
		header.addHeader("Date",format.format(new Date(System.currentTimeMillis()))+"GMT");
		return header;
	}



}
