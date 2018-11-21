package jxsource.net.proxy;

import java.nio.ByteBuffer;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

	final String src = "1234567890";
	Utils utils = new Utils();
	@Test
	public void bufferTest() { 
		ByteBuffer buffer = utils.getByteBuffer(src);
		assertTrue("buffer is null", buffer != null);
		int limit = buffer.limit();
		assertTrue("error limit", limit == 10);
		byte[] data = new byte[limit];
		buffer.get(data);
		assertTrue(new String(data).equals(src));		
	}
	
	@Test
	public void bufferCapacityTest() {
		ByteBuffer buffer = utils.getByteBuffer(src);
		assertFalse("over default capacity", buffer.capacity() < Constants.BufferCapacity);
		byte[] large = new byte[Constants.BufferCapacity+1];
		buffer = utils.getByteBuffer(large);
		assertTrue("under capacity", buffer.capacity() > Constants.BufferCapacity);
	}

	@Test
	public void byteTest() {
		ByteBuffer buffer = utils.getByteBuffer(src);
		byte[] data = utils.getBufferBytes(buffer);
		assertTrue(new String(data).equals(src));		
	}

	@Test
	public void flipTest() {
		ByteBuffer buffer = utils.getByteBuffer(src.getBytes(), false);
		buffer.flip();
		byte[] data = utils.getBufferBytes(buffer);
		assertTrue(new String(data).equals(src));		
	}

}
