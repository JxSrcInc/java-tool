package jxsource.net.proxy.http;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpMessage;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

public class HttpHeaderUtils {
	private static Logger logger = Logger.getLogger(HttpHeaderUtils.class);

	HttpMessage msg;
	
	// Not sure if all header value is case sensitive. 
	// but the values of following headers are not.
	private String[] caseInsensitiveHeader = new String[] 
			{"CONNCTION", "HOST"};
	private boolean isHeaderValueSensitive(String headerName) {
		for(int i=0; i<caseInsensitiveHeader.length; i++) {
			if(caseInsensitiveHeader[i].equals(headerName.toUpperCase())) {
				// case insensitive
				return false;
			}
		}
		return true;
	}
	
	public void setHttpMessage(final HttpMessage msg) {
		this.msg = msg;
	}
	public boolean hasHeader(String name) {
		return msg.containsHeader(name);
	}
	// false when either there is no header with name given in parameter
	// or the header does not have the value given in parameter "value"
	public boolean hasHeaderWithValue(String name, String value) {
		Header[] h = msg.getHeaders(name);
		if(h.length != 0) {
			for(int i=0; i<h.length; i++){
				String headerValue = h[i].getValue();
				if(!isHeaderValueSensitive(name)) {
					value = value.toLowerCase();
					headerValue = headerValue.toLowerCase();
				}
				if(headerValue.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	public String getHeaderAndValue(String name) {
		Header[] h = msg.getHeaders(name);
		int count = 0;
		if(h.length != 0) {
			String val = "";
			for(int i=0; i<h.length; i++){
				val += h[i].getValue();
				if(count < h.length-1) {
					val += ", ";
				}
			}
			return val;
		}
		return null;
	}
	
	public HttpMessage replaceAllHeaders(String toSearch, String toReplace) {
		HeaderIterator hi = msg.headerIterator();
		List<String> hNames = new ArrayList<String>();
		List<String> hValues = new ArrayList<String>();
		while(hi.hasNext()) {
			Header h = hi.nextHeader();
			String name = h.getName();
			String value = h.getValue();
			if(value.indexOf(toSearch) != -1) {
				hi.remove();
				value = value.replaceAll(toSearch, toReplace);
				hNames.add(name);
				hValues.add(value);
			}
		}
		for(int i=0; i<hNames.size(); i++) {
			msg.addHeader(hNames.get(i), hValues.get(i));
		}
		return msg;
	}

	public ContentType getContentType() {
		Header contentType = msg.getFirstHeader("Content-Type");
		if (contentType == null) {
			return null;
		}
		String strContentType = contentType.getValue();
		int i = strContentType.indexOf(';');
		String mimeType = strContentType;
		Charset charset = null;
		if (i != -1) {
			mimeType = strContentType.substring(0, i).trim();
			try {
				String charsetName = contentType.getElements()[0]
						.getParameterByName("charset").getValue();
				charset = Charset.forName(charsetName);
			} catch (Exception e) {
				logger.warn("Unsupported Charset: " + strContentType);
			}
		}
		return ContentType.create(mimeType, charset);
	}

}
