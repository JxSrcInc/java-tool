package jxsource.net.proxy;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import jxsource.net.proxy.SocketTestChannel;
import jxsource.util.bytearray.ByteArray;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.*;

@PrepareForTest(SocketChannel.class)
@RunWith(PowerMockRunner.class)  
public class SocketChannelTest extends SocketTestChannelBase{
	byte[] bufSrc = "1234567890".getBytes();
	SocketChannel channel;
	@Before
	public void init() throws IOException {
		prepareSocketTestChannel();
		channel = SocketChannel.open();
		assertTrue("not socket channel", channel instanceof SocketTestChannel);
	}
	@Test
	public void simpleReadTest() throws IOException {
		prepareSockestChannelInputStream(channel, bufSrc);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int i = channel.read(buffer);
		buffer.flip();
		assertTrue("different length", i == bufSrc.length);
		byte[] bufDst = new byte[i];
		buffer.get(bufDst);
		assertTrue("different value",ByteArray.equal(bufSrc, bufDst));
		
		buffer.clear();
		bufSrc = "ASDFGH".getBytes();
		prepareSockestChannelInputStream(channel, bufSrc);
		i = channel.read(buffer);
		buffer.flip();
		assertTrue("different length", i == bufSrc.length);
		bufDst = new byte[i];
		buffer.get(bufDst);
		assertTrue("different value",ByteArray.equal(bufSrc, bufDst));
	}
	@Test
	public void closeReadChannelTest() throws IOException {
		prepareSockestChannelInputStream(channel, bufSrc);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int len = 0;
		for(int i=0; i<10 || len < bufSrc.length; i++) {
			int k = channel.read(buffer);
			buffer.clear();
			if(k != -1) {
				len += k;
			} else {
				assertTrue(true);
				return;
			}
		}
		assertTrue("end test abnormal.",false);
		
	}
	@Test
	public void smallBufferReadTest() throws IOException {
		prepareSockestChannelInputStream(channel, bufSrc);
		ByteBuffer buffer = ByteBuffer.allocate(5);
		int len = 0;
		int count = 0;
		for(int i=0; i<10 && len < bufSrc.length; i++) {
			int k = channel.read(buffer);
			buffer.clear();
			count++;
			len += k;
		}
		assertTrue("count != 2.",count == 2);
	}
	
	@Test
	public void simpleWriteTest() throws IOException {
		ByteArrayOutputStream outputStream = prepareSockestChannelOutputStream(channel);
		ByteBuffer buffer = ByteBuffer.allocate(bufSrc.length);
		buffer.put(bufSrc);
		buffer.flip();
		int i = channel.write(buffer);
		outputStream.close();
		assertTrue("write different length", i == bufSrc.length);
		assertTrue("write different data", ByteArray.equal(bufSrc, outputStream.toByteArray()));	
	}
	@Test
	public void multiWriteTest() throws IOException {
		ByteArrayOutputStream outputStream = prepareSockestChannelOutputStream(channel);
		ByteArray inSrc = new ByteArray();
		inSrc.append(bufSrc);
		byte[] secondSrc = "ABCDEFG".getBytes();
		inSrc.append(secondSrc);
		prepareSockestChannelInputStream(channel, inSrc.getArray());
		ByteBuffer buffer = ByteBuffer.allocate(bufSrc.length);
		buffer.put(bufSrc);
		buffer.flip();
		int i = channel.write(buffer);
		assertTrue("write different length", i == bufSrc.length);
		buffer.clear();
		buffer.put(secondSrc);
		buffer.flip();
		i += channel.write(buffer);
		assertTrue("write different length", i == bufSrc.length+secondSrc.length);		
		assertTrue("write different data", ByteArray.equal(inSrc.getArray(), outputStream.toByteArray()));	
	}

}
