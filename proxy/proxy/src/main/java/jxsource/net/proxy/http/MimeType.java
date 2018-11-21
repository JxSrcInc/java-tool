package jxsource.net.proxy.http;
public class MimeType {
	private String type;
	private String subType;
	private String charset;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getValue() {
		return type+'/'+subType;
	}
}
