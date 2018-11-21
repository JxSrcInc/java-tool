package jxsource.net.proxy.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxsource.net.proxy.UrlInfo;
import jxsource.net.proxy.Utils;
import jxsource.net.proxy.exception.ConnectStopException;
import jxsource.net.proxy.http.exception.MessageHeaderException;
import jxsource.util.bytearray.ByteArray;

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
	private Utils utils = new Utils();
	public static final String getHttpFormatDate() {
		return httpDateformat.format(new Date(System.currentTimeMillis()))+"GMT";
	}
	public static HttpResponse getHttpResponse(int status, String message) {
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, status, message);
		response.addHeader("Server","JxSource HttpProxy Server");
		response.addHeader("Date",getHttpFormatDate());
		return response;
	}

	public ByteArray getMessageHead(SocketChannel socketChannel) {
		ByteBuffer buffer = ByteBuffer.allocate(1);
		boolean validHeader = false;
		ByteArray byteArray = new ByteArray();
		// read one byte
		int i = 0;
		int countZero = 0;
		try {
			while ((i = socketChannel.read(buffer)) != -1) {
				if (i == 0) {
					System.err.println(getClass().getName()
							+ ": read 0 byte from buffer. "+socketChannel);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					countZero++;
					if(countZero < 3) {
						continue;
					} else {
						break;
					}
				}
				buffer.flip();
				byte data = buffer.get();
				byteArray.append(data);
				if (byteArray.length() < 4) {
					// clear buffer for next read
					buffer.clear();
					continue;
				}
				// start to compare the last four bytes
				int offset = byteArray.length() - 4;
				byte[] last4bytes = byteArray.subArray(offset);
				boolean endHttpRequest = true;
				for (int k = 0; k < 4; k++) {
					if (last4bytes[k] != EndHttpRequest[k]) {
						// the last four bytes are not CRLECRLE
						endHttpRequest = false;
						// break for loop
						break;
					}
				}
				if (!endHttpRequest) {
					buffer.clear();
				} else {
					validHeader = true;
					// break while loop
					break;
				}
			} // end while loop
			if (validHeader) {
				return byteArray;
			} else {
				if (byteArray.length() == 0) {
					throw new MessageHeaderException(
							"Zero bytes in socket channel when passing Http message header",
							socketChannel);
				} else {
					throw new MessageHeaderException(
							"Invalid SocketChannel data for Message Header: "
									+ byteArray, socketChannel);
				}
			}
		} catch (IOException e) {
			throw new MessageHeaderException(
					"Error when processing Message Header. ", e, socketChannel);
		}
	}

	public HttpRequest getHttpRequest(SocketChannel socketChannel)
			throws IOException, HttpException {
		ByteArray byteArray = getMessageHead(socketChannel);
		return getHttpRequest(byteArray);
	}

	public HttpRequest getHttpRequest(ByteArray byteArray) throws IOException,
			HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionInputBufferImpl inbuffer = new SessionInputBufferImpl(metrics,
				byteArray.length());
		inbuffer.bind(byteArray.getByteArrayInputStream());
		DefaultHttpRequestParser parser = new DefaultHttpRequestParser(inbuffer);
		HttpRequest request = parser.parse();
		return request;
	}

	public HttpResponse getHttpResponse(SocketChannel socketChannel)
			throws IOException, HttpException {
		ByteArray byteArray = getMessageHead(socketChannel);
		return getHttpResponse(byteArray);
	}

	public HttpResponse getHttpResponse(ByteArray byteArray)
			throws IOException, HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionInputBufferImpl inbuffer = new SessionInputBufferImpl(metrics,
				byteArray.length());
		inbuffer.bind(byteArray.getByteArrayInputStream());
		DefaultHttpResponseParser parser = new DefaultHttpResponseParser(
				inbuffer);
		HttpResponse response = parser.parse();
		return response;
	}

	public RemoteRequest createRemoteRequest(SocketChannel channel)
			throws IOException, HttpException {
		HttpRequest src = getHttpRequest(channel);
		RequestLine srcRequestLine = src.getRequestLine();
		String method = srcRequestLine.getMethod();
		ProtocolVersion protocolVersion = srcRequestLine.getProtocolVersion();
		String uri = srcRequestLine.getUri();
		UrlInfo urlInfo = new UrlInfo(uri);
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
			request.addHeader("Host", urlInfo.getHost());
		}
		RemoteRequest remoteRequest = new RemoteRequest();
		remoteRequest.setRequest(request);
		remoteRequest.setUrlInfo(urlInfo);
		return remoteRequest;
	}

	public void writeRequest(HttpRequest request, SocketChannel channel)
			throws IOException, HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionOutputBufferImpl outbuffer = new SessionOutputBufferImpl(
				metrics, HttpConstants.SessionBufferSize);
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
//		channel.write(byteBuffer);
		utils.transfer(byteBuffer, channel);
		byteBuffer.clear();

	}
	public void outputResponse(HttpResponse response, OutputStream out) throws IOException, HttpException {
		HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
		SessionOutputBufferImpl outbuffer = new SessionOutputBufferImpl(metrics, HttpConstants.SessionBufferSize);
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
		SessionOutputBufferImpl outbuffer = new SessionOutputBufferImpl(metrics, HttpConstants.SessionBufferSize);
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
