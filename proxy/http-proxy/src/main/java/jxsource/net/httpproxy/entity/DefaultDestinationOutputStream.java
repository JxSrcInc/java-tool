package jxsource.net.httpproxy.entity;

import java.io.IOException;
import java.io.OutputStream;

public class DefaultDestinationOutputStream implements EntityDestinationOutputStream{
	protected OutputStream out;
	protected long len = 0;
	public DefaultDestinationOutputStream() {
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
	}
	
	public void write(byte[] buffer, int offset, int length) throws IOException {
		int position = length;
		len += position;
		out.write(buffer, offset, length);
	}
	
	// no difference between write and writeContent for DefaultDestinationOutputStream
	public void writeContent(byte[] buffer) throws IOException {
		int position = buffer.length;
		len += position;
		out.write(buffer);
	}
	
	public void writeContent(byte[] buffer, int offset, int length) throws IOException {
		int position = length;
		len += position;
		out.write(buffer, offset, length);
	}



	public void close() {
//			System.out.println("write "+len+" bytes");
	}


}
