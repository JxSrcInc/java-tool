package jxsource.net.proxy.test.https;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLConnection;

import jxsource.net.proxy.app.ChunkedEntityReader;
import jxsource.net.proxy.app.TestHttpUtils;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.UrlInfo;
import jxsource.net.proxy.http.HttpUtils;
import jxsource.util.buffer.bytebuffer.ByteArray;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class HttpsBridgeClientApp implements Runnable{
	private Socket socket;
	URLConnection conn;
	HttpUtils utils = new HttpUtils();
	TestHttpUtils testUtils = new TestHttpUtils();
	HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	
	public static void main(String[] args) {
		new Thread(new HttpsBridgeClientApp()).start();
//		new Thread(new HttpsBridgeClientApp()).start();
	}

	private String trans(String url) {
		try {
			byte[] b = getProxyResult(url);
			System.out.println(Thread.currentThread().getName()+": "+b.length);
			return Thread.currentThread().getName()+": "+new String(b);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}
	public void run() {
		try {
//			byte[] b = null;
//			b =	postProxyResult("http://localhost:10090/ContentLength");
//			System.out.println(Thread.currentThread().getName()+": "+new String(b));
//			System.out.println(trans("http://localhost:9096/ContentLength"));
//			trans("http://localhost:10090/LargeChunked");
//			trans("http://localhost/ballet/Giselle/x.mp4");
//			trans("http://www.baidu.com");
//			System.out.println(trans("http://localhost:10090/Chunked"));
//			trans("http://www.sohu.com");
//			trans("http://localhost:10090/LargeChunked");
//			System.out.println(trans("https://www.google.com"));
			trans("http://bridge__www.fidelity.com:443");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Socket getSocket() throws IOException {
		if(socket == null) {
		SocketAddress addr = new InetSocketAddress("localhost", 10080);
		socket = new Socket();
		socket.connect(addr);
		}
		return socket;
	}

	public byte[] getProxyResult(String uri) throws IOException, HttpException {
		HttpRequest request = testUtils.createHttpRequest(uri);
		System.out.println(Thread.currentThread().getName()+": "+request);
		UrlInfo connInfo = utils.getUrlInfo(uri);
		System.out.println(Thread.currentThread().getName()+": "+connInfo);
		Socket socket = getSocket();
		System.out.println(Thread.currentThread().getName()+": "+"socket="+socket.hashCode());
		OutputStream out = socket.getOutputStream();
		utils.outputRequest(request, out);
		System.out.println(Thread.currentThread().getName()+": "+"wait return from server on "+socket);
		return getResult();
	}
	
	public byte[] postProxyResult(String uri) throws IOException, HttpException {
		HttpRequest request = testUtils.createHttpRequest("POST", uri);
		request.addHeader("Content-Length", "10");
		System.out.println(Thread.currentThread().getName()+": "+request);
		UrlInfo connInfo = utils.getUrlInfo(uri);
		System.out.println(Thread.currentThread().getName()+": "+connInfo);
		Socket socket = getSocket();
		System.out.println(Thread.currentThread().getName()+": "+"socket="+socket.hashCode());
		OutputStream out = socket.getOutputStream();
		utils.outputRequest(request, out);
		byte[] data = "1234567890".getBytes();
		out.write(data);
		out.flush();
		System.out.println(Thread.currentThread().getName()+": "+"wait on return from server");
		return getResult();
	}


	public byte[] getResult() throws IOException, HttpException {
		InputStream in = socket.getInputStream();
		HttpResponse response = utils.getHttpResponse(in);
		System.out.println(Thread.currentThread().getName()+": "+response);
		headerUtils.setHttpMessage(response);
		if(headerUtils.hasHeader("Content-Length")) {
			int contentLength = Integer.parseInt(headerUtils.getHeaderAndValue("Content-Length"));
			byte[] buf = getOutput(in, contentLength);
			System.out.println(Thread.currentThread().getName()+": "+"Content-Length="+contentLength+", readBytes="+buf.length);
			return buf;
		} else 
		if(headerUtils.hasHeader("Transfer-Encoding")) {
			byte[] buf = new ChunkedEntityReader().getChunkedEntity(in).getArray();
			return buf;			
		} else {
			System.err.println(Thread.currentThread().getName()+": "+"Error!!!");
			return null;
		}
	}

	private byte[] getOutput(InputStream in, int length) throws IOException {
		byte[] b = new byte[1024 * 8 * 10];
		int i = 0;
		int count = 0;
		int processed = 0;
		ByteArray ba = new ByteArray();
		while (processed < length ) {
			i = in.read(b);
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
