package jxsource.net.httpproxy.entity;

import java.io.IOException;
import java.io.OutputStream;

public interface EntityDestinationOutputStream {
	public void setOutputStream(OutputStream out);
	// use write() to write any http message such as header, chunk header
	public void write(byte[] buffer) throws IOException;
	public void write(byte[] buffer, int offset, int length) throws IOException;
	// use writeContent() to write real http entity, not any header or chunk information
	public void writeContent(byte[] buffer) throws IOException;
	public void writeContent(byte[] buffer, int offset, int length) throws IOException;
	public void close();
}
