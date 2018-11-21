package jxsource.net.proxy.app;

import jxsource.util.buffer.bytebuffer.ByteArray;

public class ChunkUtils {
	static byte[] CRLF = ByteArray.CRLF;
	public static ByteArray getChunkHead(long size) {
		return getChunkHeadwithExtension(size, "");
	}
	/*
	 * extension must start with ';'
	 */
	public static ByteArray getChunkHeadwithExtension(long size, String extension) {
		String s = Long.toHexString(size)+extension;
		ByteArray ba = new ByteArray();
		ba.append(s.getBytes());
		ba.append(CRLF);
		return ba;
	}
	
	public static ByteArray createChunk(byte[] val) {
		ByteArray chunk = getChunkHead(val.length);
		chunk.append(val);
		chunk.append(CRLF);
		return chunk;
	}
	public static ByteArray createChunk(String val) {
		return createChunk(val.getBytes());
	}
	
	public static ByteArray createLastChunk() {
		ByteArray lastChunk = new ByteArray();
		lastChunk.append((byte)'0');
		lastChunk.append(CRLF);
		return lastChunk;
	}

	public static ByteArray createChunk(long size, byte[] val) {
		ByteArray entity = new ByteArray();
		ByteArray src = new ByteArray();
		src.append(val);
		while(size < src.length()) {
			byte[] srcChunk = src.remove((int)size);
			ByteArray chunk = createChunk(srcChunk);
			entity.append(chunk);
		}
		ByteArray chunk = createChunk(src.getArray());
		entity.append(chunk);
		entity.append(createLastChunk());
		// tailer
		entity.append("tailer:value".getBytes());
		entity.append(CRLF);
		// end chunk
		entity.append(CRLF);
		return entity;
		
	}

}
