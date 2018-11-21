package jxsource.net.proxy.http.entity.modifier;

import jxsource.net.bridge.http.BridgeContext;
import jxsource.net.bridge.http.BridgeContextHolder;
import jxsource.net.proxy.AppProperties;
import jxsource.net.proxy.Constants;
import jxsource.net.proxy.Controller;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.entity.DefaultDestinationOutputStream;
import jxsource.net.proxy.http.entity.DownloadFileManager;
import jxsource.net.proxy.http.entity.EntityDestinationOutputStream;
import jxsource.net.proxy.http.entity.EntityDestinationSocketManager;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

/*
 * Select EntityDestinationOutputStream for Http proxy
 * 
 * This manager select Buffered download stream and bypass (default) stream
 * by cacheMimeType value in app.properites file.
 * 
 * Entity bytes are saved in a buffer in BufferedDownloadDestinationOutputStream
 * for update before they are sent to client.
 * 
 */
public class BufferedEntityDestinationSocketManager implements
		EntityDestinationSocketManager {
	private static Logger logger = Logger
			.getLogger(BufferedEntityDestinationSocketManager.class);

	HttpHeaderUtils headerUtils = new HttpHeaderUtils();

	private DownloadFileManager downloadFileManager = new DownloadFileManager();
	private String bufferedMimeTypes = AppProperties.getInstance().getProperty(
			Constants.BufferedMimeType);

	public EntityDestinationOutputStream getEntityDestinationSocket(
			HttpRequest request, HttpResponse response, Controller controller) {

		String filename = downloadFileManager.getFilename(request, response,
				bufferedMimeTypes, DownloadFileManager.getCacheDir());
		logger.debug("Buffered File: " + filename);
		BridgeContext bridgeContext = BridgeContextHolder.get();
		logger.debug("BridgeContext: " + bridgeContext);
		if (bridgeContext != null && bridgeContext.getBridgeHost() != null) {
			try {
				if (filename != null) {
					System.err.println(filename);
					return new BufferedDownloadDestinationOutputStream(
							filename, controller, request.getRequestLine()
									.getUri(), response);
				}
			} catch (Exception e) {
				logger.error("Cache error for request " + request
						+ " and response = " + response, e);
			}
		}
		return new DefaultDestinationOutputStream();

	}
}
