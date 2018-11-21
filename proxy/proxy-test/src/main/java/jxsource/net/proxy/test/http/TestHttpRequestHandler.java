package jxsource.net.proxy.test.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.UrlInfo;
import jxsource.net.proxy.delegate.RequestHandler;
import jxsource.net.proxy.exception.ClientCloseException;
import jxsource.net.proxy.http.RequestInfo;
import jxsource.net.proxy.http.exception.EntityException;
import jxsource.net.proxy.http.exception.MessageHeaderException;

public class TestHttpRequestHandler implements RequestHandler{
	Logger logger = Logger.getLogger(TestHttpRequestHandler.class);
	private Socket localSocket;
	private OutputStream localOutputStream;
	private InputStream localInputStream;
	private EntityResponse response;
	
	public TestHttpRequestHandler() {
		// TODO Auto-generated constructor stub
	}
	public void setControllerManager(ControllerManager controllerManager) {
		// TODO Auto-generated method stub
		
	}
	public void setLocalSocket(Socket localSocket) throws IOException {
		this.localSocket = localSocket;	
		localOutputStream = localSocket.getOutputStream();
		localInputStream = localSocket.getInputStream();
	}
	
	public void run(RequestInfo requestInfo) {
		try {

			HttpRequest request = requestInfo.getRequest();
			logger.debug(request);
			String method = request.getRequestLine().getMethod();
			String url = request.getRequestLine().getUri();
			if(url.indexOf("http://localhost") == -1) {
				url = "http://localhost"+url;
			}
			logger.debug(url);
			UrlInfo urlInfo = new UrlInfo(url);
			String path = urlInfo.getPath();
			logger.debug("path: "+path);
			if(method.toUpperCase().equals("POST")) {
				int len = Integer.parseInt(request.getFirstHeader("Content-Length").getValue());
				byte[] buf = new byte[len];
				int i = 0;
				int processed = 0;
				while(processed < len) {
					i = localInputStream.read(buf,processed,len-processed);
					processed += i;
					System.out.println("Content: "+new String(buf));
				}
				response.write(localOutputStream);
			} 
			if(path.equalsIgnoreCase("/HeaderOnly")){
				response = new HeaderOnlyResponse();		 		
				response.write(localOutputStream);
			} else 
			if(path.equalsIgnoreCase("/LargeChunked")){
				response = new LargeChunkedEntityResponse();				
				response.write(localOutputStream);
			} else 
			if(path.equalsIgnoreCase("/Chunked")){
				response = new ChunkedEntityResponse();				
				response.write(localOutputStream);
			} else 
			if(path.equalsIgnoreCase("/Close")){
				response = new CloseEntityResponse();				
				response.write(localOutputStream);
				localSocket.close();
			} else {
				response = new ContentLengthEntityResponse();				
				response.write(localOutputStream);
			}
		} catch (Exception e) {
			throw new EntityException(e);
		}
		
	}

}
