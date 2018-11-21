package jxsource.net.proxy.app;

import java.io.IOException;
import java.io.InputStream;

import jxsource.net.proxy.http.exception.EntityException;
import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.string.StringUtils;

public class ChunkedEntityReader {
	private final byte[] CRLF = ByteArray.CRLF;
	
	ByteArray getEntityLine(InputStream in) throws IOException {
		byte[] buffer = new byte[1];
		ByteArray byteArray = new ByteArray();
		// read one byte 
		int i = 0;
		while((i=in.read(buffer)) != -1) {
			byteArray.append(buffer[0]);
			if(byteArray.length() < 2) {
				continue;
			}
			int offset = byteArray.length()-2;
			byte[] last2bytes = byteArray.subArray(offset);
			boolean endHttpRequest = true;
			for(int k=0; k<2; k++) {
				if(last2bytes[k] != CRLF[k]) {
					endHttpRequest = false;
					break;
				}
			}
			if(endHttpRequest) {
				break;
			}
		}		
		
		// Note: additional check. may remove later.
		// which guarantee the two bytes array must have value CRLF
		if(byteArray.length() < 3 && 
				!ByteArray.equal(byteArray.getArray(), ByteArray.CRLF)) {
			throw new IOException(
					"invalid chunk: chunk length less than 3. length="+byteArray.length()+
					", value="+StringUtils.convertBytesToString(byteArray.getArray()));
		}
		return byteArray;
	}
	
	Long getSize(ByteArray entityHead) {
		if(entityHead.length() == 2) {
			// See note in getEntityLine() method.
			return 0L;
		}
		StringBuffer sb = new StringBuffer();
		int size = entityHead.length()-2;
		byte semicolon = 59;
		int index = entityHead.indexOf(semicolon);
//		int index = entityHead.indexOf(';');
		if(index != -1) {
			size = index;
		}
		for(int i=0; i<size; i++) {
			sb.append((char)entityHead.get(i));
		}
		if(sb.length() == 0) {
			return 0L;
		} else {
			return Long.parseLong(sb.toString(),16);
		}
	}
	
	ByteArray procLength(long length, InputStream from) throws IOException {
		long processed = 0;
		byte[] buffer = new byte[1024*4*10];
		ByteArray data = new ByteArray();
		int countMissingByte = 0;
		while(true) {
			if(processed >= length) {
				break;
			}
			if(buffer.length > length-processed) {
				buffer = new byte[(int)(length-processed)];
			}
			int i = from.read(buffer);
			if(i > 0) {
				data.append(buffer,0,i);
				processed += i;
			} else {
				// This should not happen.
				countMissingByte++;
				if(countMissingByte > 3) {
					String err = " read zero bytes. length="+length+",processed="+processed;
					System.err.println(getClass().getName()+err);
					// TODO: use exception? but how?
					break;
				}
			}
		}
//		logger.debug("Completed entity copy: "+processed+" from "+length);
		return data;
	}

	ByteArray copyChunk(InputStream from) throws IOException {
		ByteArray chunk = new ByteArray();
		ByteArray head = getEntityLine(from);
		long length = getSize(head);
		if(length > 0) {
			chunk.append(procLength(length, from));
			procLength(2,from);
			return chunk;
		} else {
			// notify end entity body
			throw new EntityException("end chunk body");
		}
	}
	
	/*
	 * start point of ChunkedEntity
	 */
	public ByteArray getChunkedEntity(InputStream from) throws IOException {
		ByteArray entity = new ByteArray();
		while(true) {
			try {
				ByteArray chunk = copyChunk(from);
				entity.append(chunk);
			} catch(EntityException e) {
				break;
			}
		}
		copyTrailer(from);
		return entity;
	}
	void copyTrailer(InputStream from) {
		// TODO: temporary solution. 
		// assume stream will stop correctly - argument is if not,
		// how processor knows chunked entity ends.
		while(true) {
			ByteArray tailer = null;
			try {
				tailer = getEntityLine(from);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			if(tailer != null && tailer.length() == 2) {
				break;
			} else {
				System.out.println(">"+tailer) ;
			}
		}
	}
}
