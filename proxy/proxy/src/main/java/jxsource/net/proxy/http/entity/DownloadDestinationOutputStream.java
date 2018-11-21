package jxsource.net.proxy.http.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import jxsource.net.proxy.Controller;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.util.io.GZIPUtil;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

public class DownloadDestinationOutputStream implements EntityDestinationOutputStream{
	protected static Logger logger = Logger.getLogger(DownloadDestinationOutputStream.class);
	private static String tmpExt = ".jxtmp";
	protected HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	protected OutputStream out;
	protected OutputStream outFile;
	protected String filename;
	protected int count = 0;
	protected boolean socketOutputReady = true;
	protected boolean fileOutputReady = true;
	protected long len = 0;
	protected Exception socketException;
	protected Controller controller;
	// NOTE: When loading large video file, client may close its local socket for unknown reason
	// before download completes. In such a case, the remoteSocket is removed from controller
	// and saved as removedSocket to keep the file downloading in backend.
	// the controller.removeSocket() method also releases the lock on controller to allow other
	// threads to use it. However, download completes, both socket and controller should be released.
	protected Socket removedSocket;
	protected String url;
	protected boolean gzip;
	protected GZIPUtil gzipUtil = new GZIPUtil();
	protected HttpResponse response;
	public DownloadDestinationOutputStream(String filename, Controller controller, String url, HttpResponse response) {
		this.controller = controller;
		this.url = url;

		this.response = response;
		headerUtils.setHttpMessage(response);
		String contentEncoding = headerUtils.getHeaderAndValue("Content-Encoding");
//		String transferEncoding = headerUtils.getHeaderAndValue("Transfer-Encoding");
		if(contentEncoding != null && contentEncoding.toLowerCase().trim().equals("gzip")) {
			gzip = true;
		} else {
			gzip = false;
		}

		if(gzip) {
			this.filename = filename+tmpExt;				
		} else {
			this.filename = filename;
		}
		try {
			outFile = new FileOutputStream(this.filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public OutputStream getChannel() {
		return out;
	}
	public void setOutputStream(OutputStream channel) {
		if(this.out == null) {
			this.out = channel;
		} else
		if(this.out != channel) {
			// Shouldn't happen
			System.err.println("Different channels: existing="+this.out+", replace="+channel);
			// Override existing channel. TODO: handle in different ways?
			this.out = channel;
		}
	}
	
	public void write(byte[] buffer) throws IOException{
		int position = buffer.length;
		len += position;
		count += position;
		if(count > 1024*1000) {
			logger.debug("Thread Priority: "+Thread.currentThread().getPriority()+"\n\toutput "+count+" bytes (total="+len+") to "+filename);
			count = 0;
		}
		// use try block to skip file output stream error
		if(socketOutputReady) {
			try {
				out.write(buffer);
				out.flush();
			} catch (Exception e) {
				// allow file output stream continues
				logger.error("Socket output error",e);
				socketOutputReady = false;
				socketException = e;
				DownloadRegistry dr = DownloadRegistry.getInstance();
				if(dr.contains(url)) {
					System.err.println(Thread.currentThread().getName()+" ["+getClass().getName()+"] "+e.getClass()+" - "+e.getMessage());
					throw new IOException(e);					
				} else {
					dr.add(url);
					// see NOTE above the removedSocket field declaration
					removedSocket = controller.removeSocket();
					Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				}
			}
		}
		if(!socketOutputReady && !fileOutputReady) {
			throw new IOException("socket and file output error", socketException);
		}
	}
	
	public void write(byte[] buffer, int offset, int length) throws IOException {
		int position = length;
		len += position;
		count += position;
		if(count > 1024*1000) {
			logger.debug("Thread Priority: "+Thread.currentThread().getPriority()+"\n\toutput "+count+" bytes (total="+len+") to "+filename);
			count = 0;
		}
		if(socketOutputReady) {
			try {
				out.write(buffer, offset, length);
				out.flush();
			} catch (Exception e) {
				// allow file output stream continues
				logger.error("Socket output error",e);
				socketOutputReady = false;
				socketException = e;
				DownloadRegistry dr = DownloadRegistry.getInstance();
				if(dr.contains(url)) {
					System.err.println(Thread.currentThread().getName()+" ["+getClass().getName()+"] "+e.getClass()+" - "+e.getMessage());
					throw new IOException(e);					
				} else {
					dr.add(url);
					// see NOTE above the removedSocket field declaration
					removedSocket = controller.removeSocket();
					Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				}
			}
		}
		if(!socketOutputReady && !fileOutputReady) {
			throw new IOException("socket and file output error", socketException);
		}
	}

	public void writeContent(byte[] buffer) throws IOException{
		int position = buffer.length;
		len += position;
		count += position;
		if(count > 1024*1000) {
			logger.debug("Thread Priority: "+Thread.currentThread().getPriority()+"\n\toutput "+count+" bytes (total="+len+") to "+filename);
			count = 0;
		}
		// use try block to skip file output stream error
		if(fileOutputReady) {
			try {
				outFile.write(buffer, 0, position);
				outFile.flush();
			} catch (IOException e) {
				// allow socket output stream continues
				logger.error("File output error.",e);
			}
		}
		if(socketOutputReady) {
			try {
				out.write(buffer);
				out.flush();
			} catch (Exception e) {
				// allow file output stream continues
				logger.error("Socket output error",e);
				socketOutputReady = false;
				socketException = e;
				DownloadRegistry dr = DownloadRegistry.getInstance();
				if(dr.contains(url)) {
					System.err.println(Thread.currentThread().getName()+" ["+getClass().getName()+"] "+e.getClass()+" - "+e.getMessage());
					throw new IOException(e);					
				} else {
					dr.add(url);
					// see NOTE above the removedSocket field declaration
					removedSocket = controller.removeSocket();
					Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				}
			}
		}
		if(!socketOutputReady && !fileOutputReady) {
			throw new IOException("socket and file output error", socketException);
		}
	}
	
	public void writeContent(byte[] buffer, int offset, int length) throws IOException {
		int position = length;
		len += position;
		count += position;
		if(count > 1024*1000) {
			logger.debug("Thread Priority: "+Thread.currentThread().getPriority()+"\n\toutput "+count+" bytes (total="+len+") to "+filename);
			count = 0;
		}
		// use try block to skip output stream error
		if(fileOutputReady) {
			try {
				outFile.write(buffer, offset, length);
				outFile.flush();
			} catch (IOException e) {
				// allow socket output stream continues
				logger.error("File output error.",e);
				fileOutputReady = false;
			}
		}
		if(socketOutputReady) {
			try {
				if(out == null) {
					System.out.println(this.url);
				}
				out.write(buffer, offset, length);
				out.flush();
			} catch (Exception e) {
				// allow file output stream continues
				logger.error("Socket output error",e);
				socketOutputReady = false;
				socketException = e;
				DownloadRegistry dr = DownloadRegistry.getInstance();
				if(dr.contains(url)) {
					System.err.println(Thread.currentThread().getName()+" ["+getClass().getName()+"] "+e.getClass()+" - "+e.getMessage());
					throw new IOException(e);					
				} else {
					dr.add(url);
					// see NOTE above the removedSocket field declaration
					removedSocket = controller.removeSocket();
					Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				}
			}
		}
		if(!socketOutputReady && !fileOutputReady) {
			throw new IOException("socket and file output error", socketException);
		}
	}

	public void close() {
		try {
			outFile.close();
			if(gzip) {
				File tmpFile = new File(filename);
				try {
					InputStream in = new FileInputStream(tmpFile);
					byte[] ungzipData = gzipUtil.ungzip(in);
					String ungzipFilename = filename.substring(0, filename.length()-tmpExt.length());
					OutputStream out = new FileOutputStream(ungzipFilename);
					out.write(ungzipData,0,ungzipData.length);
					out.flush();
					in.close();
					out.close();
					tmpFile.delete();
					logger.debug("write "+ungzipData.length+" bytes to unzip file "+ungzipFilename);
				} catch(Exception e) {
					logger.error("Error when ungzip "+tmpFile+"\n\t"+response);;
				}
			} else {
				logger.debug("write "+len+" bytes to "+filename);				
			}
			
			// see NOTE above the removedSocket field declaration
			if(removedSocket != null) {
				controller = null;
				removedSocket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
