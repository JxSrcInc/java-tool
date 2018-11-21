package jxsource.net.proxy.http.entity.modifier;

import java.io.IOException;
import java.io.OutputStream;

import jxsource.net.proxy.http.entity.EntityDestinationOutputStream;
import jxsource.util.buffer.bytebuffer.ByteArray;

/*
 * This class 
 * 1) cache response'e entity in field "entity" as ByteArray
 * 	  so EntityProcessor can modify the cached entity before sending it to client
 * Note: it does not save the entity to file system as BufferedDownloadDestinationOutputStream
 */
public class BufferedSimpleDestinationOutputStream 
	implements EntityDestinationOutputStream, BufferedDestinationOutputStream{
	private OutputStream out;
	private ByteArray entity = new ByteArray();
	
	public ByteArray getEntity() {
		return entity;
	}
	public OutputStream getOutputStream() {
		return out;
	}
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}
	
	public void write(byte[] buffer) throws IOException {
		// skip chunk information
	}
	
	public void write(byte[] buffer, int offset, int length) throws IOException {
		// skip chunk information
	}
	
	public void writeContent(byte[] buffer) throws IOException {
		entity.append(buffer);
	}
	
	public void writeContent(byte[] buffer, int offset, int length) throws IOException {
		entity.append(buffer, offset, length);
	}


	public void close() {
	
	}
	

}
