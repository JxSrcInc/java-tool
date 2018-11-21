package jxsource.net.proxy.http.entity.modifier;

import java.io.IOException;
import java.io.OutputStream;

import jxsource.net.proxy.Controller;
import jxsource.net.proxy.http.entity.DownloadDestinationOutputStream;
import jxsource.util.buffer.bytebuffer.ByteArray;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

/*
 * This class 
 * 1) cache response'e entity in field "entity" as ByteArray
 * 	  so EntityProcessor can modify the cached entity before sending it to client
 * 2) save entity to file system using super DownloadDestinationOutputStream.
 */
public class BufferedDownloadDestinationOutputStream extends DownloadDestinationOutputStream
	implements BufferedDestinationOutputStream{
	protected static Logger logger = Logger.getLogger(BufferedDownloadDestinationOutputStream.class);
	private ByteArray entity = new ByteArray();

	public BufferedDownloadDestinationOutputStream(String filename, Controller controller, String url, HttpResponse response) {
		super(filename, controller, url, response);
		//disable socket output
		this.socketOutputReady = false;
	}

	@Override
	public void setOutputStream(OutputStream channel) {
		// set property "out" to null.
	}

	public ByteArray getEntity() {
		return entity;
	}
	@Override
	public void writeContent(byte[] buffer) throws IOException {

		entity.append(buffer);
		super.writeContent(buffer);
	}
	
	@Override
	public void writeContent(byte[] buffer, int offset, int length) throws IOException {
		entity.append(buffer, offset, length);
		super.writeContent(buffer, offset, length);
	}

}
