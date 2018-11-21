package jxsource.net.httpproxy.entity;

import jxsource.net.httpproxy.Config;
import jxsource.net.httpproxy.Constants;
import jxsource.net.httpproxy.socket.SocketAccessor;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

/*
 * Select EntityDestinationOutputStream for Http proxy
 * 
 * This manager select download stream and bypass (default) stream
 * by cacheMimeType value in app.properites file.
 */
public class ProxyHttpEntityDestinationSocketManager implements
		EntityDestinationSocketManager {
	private static Logger logger = Logger
			.getLogger(ProxyHttpEntityDestinationSocketManager.class);
	private DownloadFileManager downloadFileManager = new DownloadFileManager();
	private String cacheMimeTypes = Config.getInstance().getProperty(Constants.CacheMimeType);
	public EntityDestinationOutputStream getEntityDestinationSocket(
			HttpRequest request, HttpResponse response, SocketAccessor controller) {
		try {
			String filename = downloadFileManager.getFilename(request, response,
					cacheMimeTypes, 
					 DownloadFileManager.getCacheDir());
			if(filename != null) {
				return new DownloadDestinationOutputStream(filename,
						controller, request.getRequestLine().getUri(), response);				
			}
		} catch (Exception e) {
			logger.error("Cache error for request " + request
					+ " and response = " + response, e);
		}
		return new DefaultDestinationOutputStream();
		
	}
}
