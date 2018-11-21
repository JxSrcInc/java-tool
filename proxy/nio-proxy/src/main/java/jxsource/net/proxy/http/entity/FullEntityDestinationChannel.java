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
import java.util.Iterator;

import jxsource.net.proxy.exception.RemoteResponseException;
import jxsource.net.proxy.http.exception.EntityException;
import jxsource.util.bytearray.ByteArray;

/*
 * This class delays client to receive data
 * But it provides a way for you modify the data before send it out.
 */
public class FullEntityDestinationChannel implements EntityDestinationChannel{
	ByteArray entity = new ByteArray();
	SocketChannel channel;
	Selector selector;
	int count = 0;
	
	long len = 0;
	public FullEntityDestinationChannel() {
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
		byte[] buf = new byte[position];
		buffer.flip();
		buffer.get(buf);
		entity.append(buf);
	}
	
	public void close() {
/*		int l = 1024*8*10;
		ByteBuffer buffer = ByteBuffer.allocate(l);
		while (entity.getLimit() > 0) {
			try {
				int num = selector.select();
				Iterator<SelectionKey> selectionKeys = selector.selectedKeys()
						.iterator();
				while (selectionKeys.hasNext()) {
					SelectionKey key = selectionKeys.next();
					selectionKeys.remove();
					// Note: Looks key.channel() method must be called.
					// without this call and used saved channel object
					// may cause client fail to receive all message and result
					// in client side block.
					SocketChannel sc = (SocketChannel) key.channel();
					// keep the check below for debug
					if(channel != sc) System.err.println("Different key channel and stored channel: key.channel:"+sc+", stored channel:"+channel);
						if (key.isWritable() && EntityProcessor.To.equals(key.attachment())) {
								buffer.put(entity.remove(l));
								buffer.flip();
								// Note: when key.channel() method called,
								// you may use either channel or sc 
								channel.write(buffer);
								buffer.clear();
							
						}					
				}
			} catch(IOException e) {
				throw new EntityException(e);
			}
		}
			System.out.println("write "+entity.length()+" bytes");
*/	}


}
