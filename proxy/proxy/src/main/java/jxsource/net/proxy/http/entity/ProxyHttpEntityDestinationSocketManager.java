package jxsource.net.proxy.http.entity;

import jxsource.net.proxy.AppProperties;
import jxsource.net.proxy.Constants;
import jxsource.net.proxy.Controller;

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
	private String cacheMimeTypes = AppProperties.getInstance().getProperty(Constants.CacheMimeType);
	public EntityDestinationOutputStream getEntityDestinationSocket(
			HttpRequest request, HttpResponse response, Controller controller) {
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
