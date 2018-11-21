package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
public class SocketTestChannel extends SocketChannel{

	InputStream in;
	OutputStream out;

	public SocketTestChannel() {
		super(null);
	}
	protected SocketTestChannel(SelectorProvider provider) {
		super(provider);
		// TODO Auto-generated constructor stub
	}

	public <T> T getOption(SocketOption<T> arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<SocketOption<?>> supportedOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketChannel bind(SocketAddress local) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean connect(SocketAddress remote) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean finishConnect() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SocketAddress getLocalAddress() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnectionPending() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int read(ByteBuffer src) throws IOException {
		int i = src.capacity();
		byte[] buf = new byte[i];
		i = in.read(buf);
		if(i > 0) {
			src.put(buf,0,i);
		}
		return i;
	}

	@Override
	public long read(ByteBuffer[] dsts, int offset, int length)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> SocketChannel setOption(SocketOption<T> name, T value)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketChannel shutdownInput() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketChannel shutdownOutput() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket socket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		int i = src.limit();
		byte[] buf = new byte[i];
		ByteBuffer buffer = src.get(buf);
		i = buffer.limit();
		out.write(buf, 0, i);
		out.flush();
		return i;
	}

	@Override
	public long write(ByteBuffer[] srcs, int offset, int length)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void implCloseSelectableChannel() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void implConfigureBlocking(boolean arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	public static SocketChannel open() {
		return new SocketTestChannel(null);
	}

	public void setTestInputStream(InputStream in) {
		this.in = in;
	}
	
	public void setTestOutputStream(OutputStream out) {
		this.out = out;
	}
}
