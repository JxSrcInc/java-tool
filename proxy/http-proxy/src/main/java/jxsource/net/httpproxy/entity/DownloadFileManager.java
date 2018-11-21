package jxsource.net.httpproxy.entity;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import jxsource.net.httpproxy.Config;
import jxsource.net.httpproxy.Constants;
import jxsource.net.httpproxy.HttpHeaderUtils;

/*
 * create download filename based on http request and response
 */
public class DownloadFileManager {
	protected static Logger logger = Logger
			.getLogger(DownloadFileManager.class);
	private HttpHeaderUtils utils = new HttpHeaderUtils();

	class MimeType {
		String type;
		String subType;
		String charset;
	}

	private String downloadMimeType;
	
	public static String getCacheDir() {
		return	Config.getInstance().getProperty(
				Constants.CacheDir);
	}

	// downloadMimeType is a list of MIME type separated by ',' to save.
	// Exampel: "vidoe, image/png, text/html"
	// if it is null or "", then no save.
	// if it is "*", then save all
	public String getFilename(HttpRequest request, HttpResponse response,
			String downloadMimeType, String dir) {
		this.downloadMimeType = downloadMimeType;
		MimeType mimeType = getMimeType(response);
		utils.setHttpMessage(response);
//		String contentEncoding = utils.getHeaderAndValue("Content-Encoding");
		if (mimeType.type != null) {
			if (isCacheMimeType(mimeType)) {
				String filename = getFilename(dir, mimeType, request);
				//removed. use loganalysis tool 
				//logger.debug("\n\tCache " + filename + "\n\t"
				//		+ getCacheInfo(request, response));
				return filename;
			}
		} else
		if(downloadMimeType != null && downloadMimeType.trim().equals("*")) {
			mimeType.subType="unknown";
			mimeType.type="unknown";
			String filename = getFilename(dir, mimeType, request);
			logger.debug("\n\tCache " + filename + "\n\t"
					+ getCacheInfo(request, response));
			return filename;
		}
		return null;

	}
	private String getCacheInfo(HttpRequest request, HttpResponse response) {
		utils.setHttpMessage(request);
		String info = utils.getHeaderAndValue("Host")
				+ request.getRequestLine().getUri();
		utils.setHttpMessage(response);
		if (utils.hasHeader("Content-Length")) {
			String l = utils.getHeaderAndValue("Content-Length");
			info += ", Content-Length=" + Long.parseLong(l);
		} else if (utils.hasHeaderWithValue("Transfer-Encoding", "chunked")) {
			info += ", chuncked";
		}
		return info;
	}

	private boolean isCacheMimeType(MimeType mimeType) {
		if (downloadMimeType == null || downloadMimeType.trim().length() == 0) {
			return false;
		} else if (downloadMimeType.equals("*")) {
			return true;
		} else {
			return downloadMimeType.indexOf(mimeType.type) != -1;
		}
	}

	private String getFilename(String dirStr, MimeType mimeType, HttpRequest request) {
		dirStr += '/' + mimeType.type + '/' + mimeType.subType;
		File dir = new File(dirStr);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new RuntimeException("Error when creating " + dir.getPath());
		}
		String timeStamp = Long.toString(System.currentTimeMillis());
		String url = request.getRequestLine().getUri();
		return dirStr + '/' + EntityUtils.convertUrlToFilename(url) + '-' + timeStamp + '.' + mimeType.subType;
	}

	private MimeType getMimeType(HttpResponse response) {
		MimeType mimeType = new MimeType();
		for (Header header : response.getHeaders("Content-Type")) {
			String value = header.getValue().trim();
			int index = 0;
			if ((index = value.toLowerCase().indexOf("charset=")) >= 0) {
				mimeType.charset = value.substring(index + 8);
				value = value.substring(0, index);
			} 
			index = value.indexOf(';');
			if(index > 0) {
				value = value.substring(0,index);
			}
			if ((index = value.toLowerCase().indexOf('/')) > 0) {
				mimeType.type = value.substring(0, index);
				mimeType.subType = value.substring(index + 1);
			}
		}
		return mimeType;
	}

}
