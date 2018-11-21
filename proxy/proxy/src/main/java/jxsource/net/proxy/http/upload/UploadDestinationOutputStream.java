package jxsource.net.proxy.http.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxsource.net.proxy.AppProperties;
import jxsource.net.proxy.Constants;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.HttpUtils;
import jxsource.net.proxy.http.entity.EntityDestinationOutputStream;
import jxsource.net.proxy.http.entity.EntityUtils;

import org.apache.http.HttpRequest;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

public class UploadDestinationOutputStream implements EntityDestinationOutputStream {
	private static Logger logger = Logger.getLogger(UploadDestinationOutputStream.class);
	HttpHeaderUtils httpHeaderUtils = new HttpHeaderUtils();
	OutputStream out;
	OutputStream fileOutputStream;
	String uploadCacheFile;
	long len = 0;
	public UploadDestinationOutputStream(HttpRequest request) {
		try {
			uploadCacheFile = getFilename(request);
			fileOutputStream = new FileOutputStream(uploadCacheFile);
		} catch(IOException e) {
			logger.error("Fail to create uploadCacheFile: "+uploadCacheFile);
		}
	}
	
	public OutputStream getOutputStream() {
		return out;
	}
	public void setOutputStream(OutputStream out) {
		if(this.out == null) {
			this.out = out;
		} else
		if(this.out != out) {
			// Shouldn't happen
			System.err.println("Different channels: existing="+this.out+", replace="+out);
			// Override existing channel. TODO: handle in different ways?
			this.out = out;
		}
	}
	
	public void write(byte[] buffer) throws IOException {
		int position = buffer.length;
		len += position;
		out.write(buffer);
		if(fileOutputStream != null) {
			fileOutputStream.write(buffer);
			fileOutputStream.flush();
		}
	}
	
	public void write(byte[] buffer, int offset, int length) throws IOException {
		int position = length;
		len += position;
		out.write(buffer, offset, length);
		if(fileOutputStream != null) {
			fileOutputStream.write(buffer, offset, length);
			fileOutputStream.flush();
		}
	}
	
	// no difference between write and writeContent for DefaultDestinationOutputStream
	public void writeContent(byte[] buffer) throws IOException {
		int position = buffer.length;
		len += position;
		out.write(buffer);
		if(fileOutputStream != null) {
			fileOutputStream.write(buffer);
			fileOutputStream.flush();
		}
	}
	
	public void writeContent(byte[] buffer, int offset, int length) throws IOException {
		int position = length;
		len += position;
		out.write(buffer, offset, length);
		if(fileOutputStream != null) {
			fileOutputStream.write(buffer, offset, length);
			fileOutputStream.flush();
		}
	}



	public void close() {
		logger.info("upload "+len+" bytes cached in "+uploadCacheFile);
	}


	private String getFilename(HttpRequest request) throws IOException {
		String	uploadCacheDir = AppProperties.getInstance().getProperty(
				Constants.CacheDir)+"/upload/";
		httpHeaderUtils.setHttpMessage(request);
		ContentType contentType = httpHeaderUtils.getContentType();
		String ext = "txt";
		if(contentType == null) {
			uploadCacheDir += "unknown";
		} else {
			String mimeType = contentType.getMimeType();
			ext = mimeType;
			int i = mimeType.indexOf('/');
			if(i > 0) {
				ext = mimeType.substring(i+1);
			}
			uploadCacheDir += mimeType;
		}
			File dir = new File(uploadCacheDir);
			if (!dir.exists() && !dir.mkdirs()) {
				uploadCacheDir = null;
				throw new IOException("Error when creating " + dir.getPath());
			}
		String url = request.getRequestLine().getUri();

		return uploadCacheDir+'/'+EntityUtils.convertUrlToFilename(url)+'-'+
				System.currentTimeMillis()+"."+ext;
		
	}
}
