package jxsource.net.proxy.test.https;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLConnection;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import jxsource.net.proxy.app.ChunkedEntityReader;
import jxsource.net.proxy.app.TestHttpUtils;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.UrlInfo;
import jxsource.net.proxy.http.HttpUtils;
import jxsource.util.buffer.bytebuffer.ByteArray;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class TestHttpsClient {
	private Socket socket;
	URLConnection conn;
	HttpUtils utils = new HttpUtils();
	TestHttpUtils testUtils = new TestHttpUtils();
	HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	int testPort = 9090;
	public static void main(String[] args) {
		new TestHttpsClient().run();
	}

	public void run() {
		try {
			SocketFactory f = SSLSocketFactory.getDefault();
	        socket =
//			           (SSLSocket) f.createSocket("www.google.com", 443);
	        (SSLSocket) f.createSocket("localhost", testPort);
			byte[] b = null;
			b = getProxyResult("http://localhost:"+Integer.toString(testPort)+"/Chunked");
			System.out.println(b.length);
			System.out.println(new String(b));
			b = getProxyResult("http://localhost:"+Integer.toString(testPort)+"/ContentLength");
//			b = getProxyResult("https://www.google.com/");
			System.out.println(b.length);
			System.out.println(new String(b));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] getProxyResult(String url) throws IOException, HttpException {
//		UrlInfo urlInfo = utils.getUrlInfo(url);
//		SocketAddress addr = new InetSocketAddress(urlInfo.getHostName(), urlInfo.getPort());
		HttpRequest request = testUtils.createHttpRequest(url);
		OutputStream out = socket.getOutputStream();
		utils.outputRequest(request, out);
		System.out.println("wait on return from server");

		InputStream in = socket.getInputStream();
		HttpResponse response = utils.getHttpResponse(in);
		System.out.println(response);
		headerUtils.setHttpMessage(response);
		if(headerUtils.hasHeader("Content-Length")) {
			int contentLength = Integer.parseInt(headerUtils.getHeaderAndValue("Content-Length"));
			byte[] buf = getOutput(in, contentLength);
			System.out.println("Content-Length="+contentLength+", readBytes="+buf.length);
			return buf;
		} else 
		if(headerUtils.hasHeader("Transfer-Encoding")) {
			byte[] buf = new ChunkedEntityReader().getChunkedEntity(in).getArray();
			return buf;			
		} else {
			System.err.println("No entity Error!!!");
			byte[] buf = "Response has no message body. This message is add by Client itself.".getBytes();
			return buf;
		}
	}

	private byte[] getOutput(InputStream in, int length) throws IOException {
		byte[] b = new byte[1024 * 8 * 10];
		int i = 0;
		int count = 0;
		int processed = 0;
		ByteArray ba = new ByteArray();
		while (processed < length ) {
//			System.out.println(i);
			i = in.read(b);
			System.out.println(i+","+processed);
			if(i > 0){
				processed += i;
				ba.append(b, 0, i);
			} else 
			if(i == -1) {
				break;
			} else {
				if(count > 10) {
					break;
				} else {
					count++;
				}
			}
				
		}
		return ba.getArray();

	}


}
