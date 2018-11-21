package jxsource.net.proxy.http;

import org.apache.http.Header;
import org.apache.http.HttpMessage;

public class HttpHeaderUtils {

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

}
