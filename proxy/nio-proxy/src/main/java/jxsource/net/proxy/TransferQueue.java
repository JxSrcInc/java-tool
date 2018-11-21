package jxsource.net.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

import jxsource.util.bytearray.ByteArray;

/*
 * Not thread safe now to get better performance. 
 * To make thread safe, synchronize the queue.
 */
public interface TransferQueue {
	public boolean isEmpty(); 
	public void add(ByteBuffer buffer);
	public ByteBuffer poll(); 	// return a write-ready ByteBuffer
	public ByteBuffer peek();
	public void close();

}
