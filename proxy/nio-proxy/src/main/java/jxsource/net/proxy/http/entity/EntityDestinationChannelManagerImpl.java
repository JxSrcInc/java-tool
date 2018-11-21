package jxsource.net.proxy.http.entity;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import jxsource.net.proxy.AppProperties;
import jxsource.net.proxy.Constants;
import jxsource.net.proxy.http.HttpHeaderUtils;

public class EntityDestinationChannelManagerImpl implements
		EntityDestinationChannelManager {
	private static Logger logger = Logger
			.getLogger(EntityDestinationChannelManagerImpl.class);
	HttpHeaderUtils utils = new HttpHeaderUtils();
	class MimeType {
		String type;
		String subType;
		String charset;
	}

	private String cacheMimeType = AppProperties.getInstance().getProperty(
			Constants.CacheMimeType);

	public EntityDestinationChannel getEntityDestinationChannel(
			HttpRequest request, HttpResponse response) {
		try {
		MimeType mimeType = getMimeType(response);
		if(mimeType.type != null) {
			if(isCacheMimeType(mimeType)) {
				String filename = getFilename(mimeType);
				logger.debug("\n\tCache "+filename+"\n\t"+getCacheInfo(request, response));
				return new DownloadDestinationChannel(filename);
			}
		}
		} catch(Exception e) {
			logger.error("Cache error for request "+request+" and response = "+response, e);
		}
		return new DefaultDestinationChannel();
	}

	private String getCacheInfo(HttpRequest request, HttpResponse response) {
		utils.setHttpMessage(request);
		String info = utils.getHeaderAndValue("Host") + 
				request.getRequestLine().getUri();
		utils.setHttpMessage(response);
		if(utils.hasHeader("Content-Length")) {
			String l = utils.getHeaderAndValue("Content-Length");
			info += ", Content-Length="+Long.parseLong(l);
		} else
		if(utils.hasHeaderWithValue("Transfer-Encoding", "chunked")) {
			info += ", truncked";
		}
		return info;
	}
	private boolean isCacheMimeType(MimeType mimeType) {
		return cacheMimeType.indexOf(mimeType.type) != -1;
	}

	private String getFilename(MimeType mimeType) {
		String dirStr = AppProperties.getInstance().getProperty(
				Constants.CacheDir)
				+ '/' + mimeType.type + '/' + mimeType.subType;
		File dir = new File(dirStr);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new RuntimeException("Error when creating " + dir.getPath());
		}
		String timeStamp = Long.toString(System.currentTimeMillis());
		return dirStr + '/' + timeStamp + '.' + mimeType.subType;
	}

	private MimeType getMimeType(HttpResponse response) {
		MimeType mimeType = new MimeType();
		for (Header header : response.getHeaders("Content-Type")) {
			String value = header.getValue().trim();
			int index = 0;
			if ((index = value.toLowerCase().indexOf("charset=")) >= 0) {
				mimeType.charset = value.substring(index + 8);
			} else if ((index = value.toLowerCase().indexOf('/')) > 0) {
				mimeType.type = value.substring(0, index);
				mimeType.subType = value.substring(index + 1);
			}
		}
		return mimeType;
	}
}
