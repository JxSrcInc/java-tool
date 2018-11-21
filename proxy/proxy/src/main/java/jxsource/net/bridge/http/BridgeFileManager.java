package jxsource.net.bridge.http;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import jxsource.net.proxy.AppProperties;
import jxsource.net.proxy.Constants;
import jxsource.net.proxy.http.entity.DownloadFileManager;

public class BridgeFileManager extends DownloadFileManager{
	protected static Logger logger = Logger.getLogger(BridgeFileManager.class);
	public String getBridgeFilename(HttpRequest request, HttpResponse response, String type) {
		String downloadMimeType = AppProperties.getInstance().getProperty(Constants.BridgeMimeType); 
 		String dir = AppProperties.getInstance().getProperty(Constants.BridgeDir);
		String filename = super.getFilename(request, response, downloadMimeType, dir);
		int i = filename.lastIndexOf(".");
		return filename.substring(0, i)+'.'+type+filename.substring(i);
	}
	
	public void save(String filename, byte[] content) {
		try {
			OutputStream out = new FileOutputStream(filename);
			out.write(content);
			out.flush();
			out.close();
		} catch(Exception e) {
			logger.error("Fail to save "+filename, e);
		}
	}
}
