package jxsource.net.httpproxy.io;

import java.io.IOException;
import java.io.InputStream;

import jxsource.util.buffer.bytebuffer.ByteArray;

public class HttpInputStream extends InputStream{
	private InputStream src;
	private ByteArray buf = new ByteArray();
	private boolean closed;
	private int inputSize = 1024*16;
	private byte[] tmp = new byte[inputSize];
	private final byte[] EndHttpRequest = new byte[] { 13, 10, 13, 10 };
	// index after byte[] of EndHttpRequest
	private int httpHeaderEndIndex = -1;
	public HttpInputStream(InputStream src) {
		this.src = src;
	}
	
	// return index of http header end in buf
	// or -1 if src input stream closed
	public int loadHttpHeader() throws IOException {
		while(httpHeaderEndIndex == -1) {
			// TODO: add available check
			int len = src.read(tmp);
			if(len != -1) {
				buf.append(tmp,0,len);
				httpHeaderEndIndex = buf.indexOf(EndHttpRequest)+4;
				// break loop
			} else {
				closed = true;
				// force to break loop
				break;
			}
		}
		return httpHeaderEndIndex;
	}
	// this provide better performance when first call loadHttpHeader()
	// and then pass a parameter byte[] with length returned by loadHttpHeader()
	public int fillHttpHeader(byte[] b) throws IOException {
		if(httpHeaderEndIndex == -1 // http header not loaded initially
				&& loadHttpHeader() == -1) {// src input stream closed during http header loading process
			closed = true;
			return -1;
		} 
		// at here, http headed loaded into buf
		if(b.length < httpHeaderEndIndex) {
			throw new IOException("byte[] length "+b.length+" is smaller than http header length "+httpHeaderEndIndex);
		}
		int headerLength = buf.fillin(b,0,httpHeaderEndIndex); 
		// NOTE: headerLength must equals httpHeaderEndIndex
		// double check 
		if(headerLength != httpHeaderEndIndex) {
			throw new IOException("error when loading http header.");
		}
		buf.delete(headerLength);
		// reset httpHeaderEndIndex because http header removed from buf.
		httpHeaderEndIndex = -1;
		return headerLength;
	}
	// one call method
	public ByteArray getHttpHeader() throws IOException {
		int byteArrayLength = loadHttpHeader();
		if(byteArrayLength == -1) {
			throw new IOException("Input Stream closed.");
		}
		ByteArray ba = new ByteArray();
		ba.append(buf.remove(httpHeaderEndIndex));
		httpHeaderEndIndex = -1;
		return ba;
	}

	/*
	 * Load bytes from src stream into buf.
	 * 
	 * return value should be greater than or equal to parameter length
	 * but it may less than parameter length 
	 * if src stream has less bytes before it closes
	 */
	private int load(int length) throws IOException {
		int loaded = 0;
		while(loaded < length) {
			int i = src.read(tmp);
			if(i > 0) {
				buf.append(tmp, 0, i);
				loaded += i;
			} else 
			if(i == -1) {
				closed = true;
				break;
			}
		}
		return loaded;
	}
	@Override
	public int read() throws IOException {
		throw new IOException("Not support.");
	}

	@Override
	public int read(byte[] b) throws IOException {
		if(closed) {
			return -1;
		} 
		if(0 < buf.getLimit() && buf.getLimit() < b.length) {
			int len = buf.getLimit();
			buf.fillin(b, 0, len);
			buf.delete(len);
			return len;
		} else {
			int len = src.read(b);
			if(len == -1) {
				closed = true;
			}
			return len;
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(closed) {
			return -1;
		} 
		if(0 < buf.getLimit() && buf.getLimit() < b.length) {
			int length = buf.getLimit();
			buf.fillin(b, off, length);
			buf.delete(length);
			return length;
		} else {
			int length = src.read(b, off, len);
			if(length == -1) {
				closed = true;
			}
			return length;
		}
	}

	@Override
	public long skip(long n) throws IOException {
		throw new IOException("Not support.");
	}

	@Override
	public int available() throws IOException {
		if(buf.getLimit() > 0) {
			return buf.getLimit();
		} else {
			return src.available();
		}
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		closed = true;
		buf.delete(buf.getLimit());
		src.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new RuntimeException("Not support.");
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new IOException("Not support.");
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO
	}

}
