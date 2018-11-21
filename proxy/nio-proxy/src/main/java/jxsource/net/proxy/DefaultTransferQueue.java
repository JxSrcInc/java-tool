package jxsource.net.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

import jxsource.net.proxy.http.HttpConstants;
import jxsource.util.bytearray.ByteArray;

/*
 * Not thread safe now to get better performance. 
 * To make thread safe, synchronize the queue.
 */
public class DefaultTransferQueue implements TransferQueue {
	private Queue<ByteBuffer> bufferQueue = new LinkedList<ByteBuffer>();

	public DefaultTransferQueue() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean isEmpty() {
		return bufferQueue.isEmpty();
	}
	
	public void add(ByteBuffer buffer) {
		bufferQueue.add(buffer);
	}
	
	public ByteBuffer poll() {
		ByteBuffer buffer = bufferQueue.poll();
		return buffer;
	}
	
	public ByteBuffer peek() {
		return bufferQueue.peek();
	}
	
	public void close() {
		
	}
}
