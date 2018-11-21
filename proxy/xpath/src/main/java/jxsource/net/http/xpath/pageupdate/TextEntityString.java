package jxsource.net.http.xpath.pageupdate;

import java.nio.charset.Charset;

public class TextEntityString {

	private byte[] entityBytes;
	private Charset charset;
	private String value;
	private boolean valid;
	
	TextEntityString() {}
	
	public byte[] getEntityBytes() {
		return entityBytes;
	}

	void setEntityBytes(byte[] entityBytes) {
		this.entityBytes = entityBytes;
	}

	public Charset getCharset() {
		return charset;
	}

	void setCharset(Charset charset) {
		this.charset = charset;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}


}
