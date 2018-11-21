package jxsource.net.proxy.http;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import jxsource.net.proxy.SocketChannelTest;
import jxsource.net.proxy.SocketTestChannel;
import jxsource.net.proxy.SocketTestChannelBase;
import jxsource.net.proxy.http.entity.BlockingEntityProcessor;
import jxsource.net.proxy.http.entity.EntityProcessor;
import jxsource.net.proxy.http.entity.NoneBlockingEntityProcessor;
import jxsource.util.bytearray.ByteArray;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.*;

@PrepareForTest(SocketChannel.class)
@RunWith(PowerMockRunner.class)  
public class ChunkTest extends SocketTestChannelBase{
	byte[] CRLE = ByteArray.CRLF;
	BlockingEntityProcessor ep = new BlockingEntityProcessor();
	ByteArray chunkHeader;
	ByteArray chunkExtendedHeader;
	byte[] chunkBody = "1234567890".getBytes();
	
	@Before
	public void init() throws IOException {
		createChunkHeader(100);
		createChunkExtendedHeader(100);
		prepareSocketTestChannel();
	}
	private ByteArray createChunkHeader(long src) {
		String s = Long.toHexString(src);
		chunkHeader = new ByteArray();
		chunkHeader.append(s.getBytes());
		chunkHeader.append(CRLE);
		return chunkHeader;
	}
	private ByteArray createChunkExtendedHeader(long src) {
		String s = Long.toHexString(src)+";a=b";
		chunkExtendedHeader = new ByteArray();
		chunkExtendedHeader.append(s.getBytes());
		chunkExtendedHeader.append(CRLE);
		return chunkExtendedHeader;
	}
	
	private ByteArray createChunk(byte[] chunkBody) {
		ByteArray chunk = new ByteArray();
		chunk.append(createChunkHeader(chunkBody.length));
		chunk.append(chunkBody);
		return chunk;
		
	}

	@Test
	public void chunkHeadTest() {
		long target = ep.getChunkSize(chunkHeader);
		assertTrue(target == 100);
	}
	@Test
	public void chunkHeadwithExtensionTest() {
		long target = ep.getChunkSize(chunkExtendedHeader);
		assertTrue(target == 100);
	}
	@Test
	public void entityLineTest() throws IOException {
		SocketChannel channel = SocketChannel.open();
		assertTrue(channel instanceof SocketTestChannel);
		prepareSockestChannelInputStream(channel,chunkExtendedHeader.getArray());
		ByteArray ba = ep.getLine(channel);
		assertTrue(ByteArray.equal(ba.getArray(), chunkExtendedHeader.getArray()));
	}
/*	
	@Test
	public void copyChunkTest() throws IOException {
		SocketChannel from = SocketChannel.open();
		assertTrue(from instanceof SocketTestChannel);
		prepareSockestChannelInputStream(from,createChunk(chunkBody).getArray());
		
		SocketChannel to = SocketChannel.open();
		ByteArrayOutputStream out = prepareSockestChannelOutputStream(to);
		ep.procLength(13, from, to);
		out.close();
		System.out.println(new String(out.toByteArray()));
//		assertTrue(ByteArray.equal(out.toByteArray(), chunkBody.getArray()));
	}
*/
/*	
	@Test
	public void chunkTest() throws IOException {
		ByteArray chunkedEntity = createChunk(345, (byte)90);
		ByteArrayInputStream from = new ByteArrayInputStream(chunkedEntity.getArray());
		ByteArrayOutputStream to = new ByteArrayOutputStream();
		long length = ce.copyChunk(from, to);
		assertTrue(length == 345);
		from.close();
		to.close();
		assertTrue(ByteArray.equal(chunkedEntity.getArray(), to.toByteArray()));
	}

	@Test
	public void chunkEntityTest() throws IOException {
		ByteArray chunkedEntity = createChunk(345, (byte)90);
		chunkedEntity.append(createChunk(3856, (byte) 23));
		chunkedEntity.append(createChunk(0, (byte) 0));
		ByteArrayInputStream from = new ByteArrayInputStream(chunkedEntity.getArray());
		ByteArrayOutputStream to = new ByteArrayOutputStream();
		ce.copyChunkedEntity(from, to);
		from.close();
		to.close();
		assertTrue(ByteArray.equal(chunkedEntity.getArray(), to.toByteArray()));
	}
	@Test
	public void emptyChunkEntityTest() throws IOException {
		ByteArray chunkedEntity = createChunk(345, (byte)90);
		chunkedEntity.append(ByteArray.CRLF);
		ByteArrayInputStream from = new ByteArrayInputStream(chunkedEntity.getArray());
		ByteArrayOutputStream to = new ByteArrayOutputStream();
		ce.copyChunkedEntity(from, to);
		from.close();
		to.close();
		assertTrue(ByteArray.equal(chunkedEntity.getArray(), to.toByteArray()));
	}
	
*/
}
