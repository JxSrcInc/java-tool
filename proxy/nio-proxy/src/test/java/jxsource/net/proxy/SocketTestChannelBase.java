package jxsource.net.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;

import org.powermock.api.mockito.PowerMockito;

public class SocketTestChannelBase {

	public void prepareSocketTestChannel() throws IOException {
		SocketTestChannel socketTestChannel = new SocketTestChannel();
		PowerMockito.mockStatic(SocketChannel.class);

		PowerMockito.when(SocketChannel.open()).thenReturn(
					socketTestChannel);
//		return socketTestChannel;
	}

	// method to setup read source
	public InputStream prepareSockestChannelInputStream(SocketChannel socketChannel, byte[] src) {
		InputStream inputStream = new ByteArrayInputStream(src);
		((SocketTestChannel)socketChannel)
				.setTestInputStream(inputStream);
		return inputStream;
	}
	public ByteArrayOutputStream prepareSockestChannelOutputStream(SocketChannel socketChannel) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		((SocketTestChannel)socketChannel)
				.setTestOutputStream(outputStream);
		return outputStream;
	}

}
