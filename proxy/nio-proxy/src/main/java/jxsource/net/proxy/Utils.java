package jxsource.net.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxsource.net.proxy.exception.TransferException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;

public class Utils {
	public static final String OP_ACCEPT = "OP_ACCEPT";
	public static final String OP_CONNECT = "OP_CONNECT";
	public static final String OP_READ = "OP_READ";
	public static final String OP_WRITE = "OP_WRITE";

	public List<String> getSelectionKeyOptionNames(int options) {
		List<String> opNames = new ArrayList<String>();
		if((options|SelectionKey.OP_ACCEPT) != 0) {
			opNames.add(OP_ACCEPT);
		}
		if((options|SelectionKey.OP_CONNECT) != 0) {
			opNames.add(OP_CONNECT);
		}
		if((options|SelectionKey.OP_READ) != 0) {
			opNames.add(OP_READ);
		}
		if((options|SelectionKey.OP_WRITE) != 0) {
			opNames.add(OP_WRITE);
		}
		return opNames;
	}
	public List<String> getSelectionKeyEnables(SelectionKey key) {
		List<String> enables = new ArrayList<String>();
		if (key.isReadable()) {
			enables.add("Readable");
		} else
		if (key.isWritable()) {
			enables.add("Writable");
		} else
		if (key.isAcceptable()) {
			enables.add("Acceptable");						
		} else
		if (key.isConnectable()) {
			enables.add("Connectable");
		} else
		if (key.isValid()) {
			enables.add("Valid");
		}
		return enables;
	}
	public ByteBuffer getByteBuffer(byte[] buf, int capacity) {
		return getByteBuffer(buf, capacity, true);
	}
	public ByteBuffer getByteBuffer(byte[] buf, int capacity, boolean flip) {
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		buffer.put(buf);
		if(flip) {
			buffer.flip();
		}
		return buffer;
	}
	public ByteBuffer getByteBuffer(byte[] buf) {
		return getByteBuffer(buf, Math.max(buf.length, Constants.BufferCapacity));
	}
	public ByteBuffer getByteBuffer(byte[] buf, boolean flip) {
		return getByteBuffer(buf, Math.max(buf.length, Constants.BufferCapacity), flip);
	}
	public ByteBuffer getByteBuffer(String str) {
		if(str == null) {
			throw new RuntimeException("Cannot convert null to ByteBuffer.");
		}
		return getByteBuffer(str.getBytes());
	}
	public byte[] getBufferBytes(ByteBuffer buffer) {
		byte[] buf = new byte[buffer.limit()];
		if(buffer.position() != 0) {
			buffer.flip();
		}
		buffer.get(buf);
		return buf;
	}
	public String getBufferStrings(ByteBuffer buffer) {
		return new String(getBufferBytes(buffer));
	}

	public void closeSocketChannel(SocketChannel channel) {
		try {
			channel.shutdownInput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			channel.shutdownOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void transfer(ByteBuffer buffer, SocketChannel dst) throws IOException {
			dst.write(buffer);
			while (buffer.remaining() > 0) {
//				logger.warn(buffer.remaining()+" bytes wait to write.");
				try {
					Thread.sleep(500);
				} catch(InterruptedException ie) {}
				dst.write(buffer);
			}
	}

}
