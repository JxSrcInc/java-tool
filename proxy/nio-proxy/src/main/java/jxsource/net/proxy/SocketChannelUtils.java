package jxsource.net.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

public class SocketChannelUtils {
	private Logger logger = Logger.getLogger(SocketChannelUtils.class);
	public void closeSocketChannel(SocketChannel channel, Selector... selectors) {
		logger.info("shutdown: "+channel);
		for(Selector selector: selectors) {
			SelectionKey key = channel.keyFor(selector);
			if(key != null) key.cancel();			
		}
		try {
			channel.shutdownInput();
		} catch (Exception e) {}
		try {
			channel.shutdownOutput();
		} catch (Exception e) {}
		try {
			channel.close();
		} catch (Exception e) {}					
	}
	public void writeByteArrayToChannel(byte[] data, SocketChannel channel) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
		byteBuffer.put(data);
		byteBuffer.flip();
		channel.write(byteBuffer);
		byteBuffer.clear();
	}

}
