package jxsource.net.proxy.test.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
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

public class TestHttpClient {
	private Socket socket;
	URLConnection conn;
	HttpUtils utils = new HttpUtils();
	TestHttpUtils testUtils = new TestHttpUtils();
	HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	
	public static void main(String[] args) {
		System.setProperty("sockProxyHost", "localhost");
		System.setProperty("socksProxyPort", "10080");
		new TestHttpClient().run();
/*		try {
			URL url = new URL("http://192.168.1.9:7001/DemoRestful/resources/Root/hello");
			url.openConnection().connect();
			System.out.println("Complete");
		} catch(Exception e) {
			e.printStackTrace();
		}
*/	}

	public void run() {
		try {
			byte[] b = null;
//			b = getProxyResult("http://localhost:10090/HeaderOnly");
//			System.out.println(b.length);
//			System.out.println(new String(b));
//			b = getProxyResult("http://localhost:10090/ContentLength");
			b = getProxyResult("http://localhost:10090/ChunkedEntity");
//			b = getProxyResult("http://192.168.1.9:7001/DemoRestful/resources/Root/hello");
			System.out.println(b.length);
			System.out.println(new String(b));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] getProxyResult(String url) throws IOException, HttpException {
		UrlInfo urlInfo = utils.getUrlInfo(url);
		SocketAddress dest = new InetSocketAddress(urlInfo.getHostName(), urlInfo.getPort());
		HttpRequest request = testUtils.createHttpRequest(url);
//		Socket socket = new Socket();

//		SocketAddress addrProxy = new InetSocketAddress("localhost", 10080);
//		Proxy proxy = new Proxy(Proxy.Type.SOCKS, addrProxy);
		Socket socket = new Socket();//proxy);
		socket.connect(dest);
		System.out.println(socket.isBound()+","+socket.isConnected());
//		socket.connect(addr);
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
