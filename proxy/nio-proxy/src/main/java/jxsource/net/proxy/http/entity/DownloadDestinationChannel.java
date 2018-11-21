package jxsource.net.proxy.http.entity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import jxsource.net.proxy.Utils;

import org.apache.log4j.Logger;

public class DownloadDestinationChannel implements EntityDestinationChannel{
	private static Logger logger = Logger.getLogger(DownloadDestinationChannel.class);
	private static int index = 0;
	SocketChannel channel;
	OutputStream out;
	Selector selector;
	String filename;
	int count = 0;
	Utils utils = new Utils();
	
	long len = 0;
	public DownloadDestinationChannel(String filename) {
		try {
			this.filename = filename;//"c:/temp/download/"+(index++)+".flv";
			out = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public DownloadDestinationChannel() {
		try {
			this.filename = "c:/temp/download/"+(index++)+".flv";
			out = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public SocketChannel getChannel() {
		return channel;
	}
	public void setSocketChannel(SocketChannel channel) {
		if(this.channel == null) {
			this.channel = channel;
		} else
		if(this.channel != channel) {
			// Shouldn't happen
			System.err.println("Different channels: existing="+this.channel+", replace="+channel);
			// Override existing channel. TODO: handle in different ways?
			this.channel = channel;
		}
	}
	
	public SelectionKey register(Selector selector, int options) throws ClosedChannelException {
		this.selector = selector;
		return channel.register(selector, options);
	}
	
	public void write(ByteBuffer buffer) throws IOException {
		int position = buffer.position();
		len += position;
		count += position;
		if(count > 1024*1000) {
			logger.debug("\n\toutput "+count+" bytes (total="+len+") to "+filename);
			count = 0;
		}
		byte[] buf = new byte[position];
		buffer.flip();
		buffer.get(buf);
		// use try block to skip output stream error
		try {
			out.write(buf, 0, position);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer.rewind();
//		channel.write(buffer);
		utils.transfer(buffer, channel);
	}
	
	public void close() {
		try {
			out.close();
			logger.debug("write "+len+" bytes to "+filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
