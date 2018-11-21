package jxsource.net.proxy.http.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import jxsource.net.proxy.Constants;
import jxsource.net.proxy.http.HttpConstants;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.exception.EntityException;
import jxsource.net.proxy.http.exception.MessageHeaderException;
import jxsource.util.bytearray.ByteArray;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.log4j.Logger;

public class BlockingEntityProcessor implements EntityProcessor{
	final byte[] CRLF = ByteArray.CRLF;
	public final static String EntiryComplete = "Complete";
	public final static String EntityTimeOut = "TimeOut";
	public final static String EntityClosed = "Closed";
	public final static String NoEntity = "NoEntity";
	
	private Logger logger = Logger.getLogger(BlockingEntityProcessor.class);
	HttpHeaderUtils headerSearch = new HttpHeaderUtils();
	int capacity = HttpConstants.EntityBufferSize;

	void setEntityBufferSize(int capacity) {
		this.capacity = capacity;
	}
	public EntityStatus processEntity(HttpMessage message, SocketChannel from, EntityDestinationChannel to) throws IOException {
		// use a new selector for each entity
		// use a new TransferQueue for each entity
		headerSearch.setHttpMessage(message);

		long length = 0l;
		if(message.containsHeader("Content-Length")) {
			Header contentLength = message.getFirstHeader("Content-Length");
			length = Long.parseLong(contentLength.getValue());
		}
		if(headerSearch.hasHeaderWithValue("Transfer-Encoding", "chunked")) {
			long processed = procChunkedEntity(from, to);
			return new EntityStatus(processed, EntiryComplete);
		} else {
			if(headerSearch.hasHeaderWithValue("Connection", "close")) {
				if(length > 0) {
					long processed = procLength(length, from, to);
					if(length != processed) {
						logger.error(getClass().getName()+": different bytes in closed connection: requested="+length+", processed="+processed);
					}
					return new EntityStatus(processed, EntityClosed, length );
				} else {
					long processed = procLength(Constants.InfiniteLong, from, to);
					return new EntityStatus(processed, EntityClosed, Constants.InfiniteLong);
				}
			} else
			if(length > 0) {
				long processed = procLength(length, from, to);
				if(length != processed) {
					logger.error(getClass().getName()+": length="+length+",processed="+processed);
				}
				return new EntityStatus(processed, EntiryComplete, length);
			} else {
				// server close connection to end transaction
				// but without header Connection:close
				
				// TODO: the above case need more design.
//				waitTime = HttpConstants.CloseEntityTimeOut;
//				long processed = procLength(Constants.InfiniteLong, from, to);
				long processed = 0;
				return new EntityStatus(processed, NoEntity, 0);
			}
		}
	}

	public long procLength(long length, SocketChannel from, 
			EntityDestinationChannel to) throws IOException {
		long processed = 0;
		boolean complete = false;
		while (processed < length) {
			int bufferSize = (int)Math.min((length-processed), capacity);
			ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
			try {
					int i = from.read(buffer);
						processed += i;
						to.write(buffer);
				
			} catch(IOException e) {
				throw new EntityException(e);
			}
		}
		if(processed != length) {
			logger.error("***** different content length: processed="+processed+", length="+length);
		}
		return processed;
	}

	long procChunkedEntity(SocketChannel from, EntityDestinationChannel to) {
		long processed = 0;
		long entitySize = 0;
		boolean complete = false;
		boolean isChunk = true;
		while (!complete) {
			try {
						if(isChunk) {
							// proc chunk header
							ByteArray chunkHeader = getLine(from);
							ByteBuffer buffer = ByteBuffer.allocate(chunkHeader.length());
							buffer.put(chunkHeader.getArray());
							to.write(buffer);
							processed += chunkHeader.length();
							// proc chunk body
							long size = getChunkSize(chunkHeader);
							entitySize += size;
//							logger.debug("read header: chunkzise="+size);
							processed += procLength(size+2, from, to);
							if(size == 0) {
								isChunk = false;
							}
						} else {
							// end chunk body
							ByteArray chunkTailer = getLine(from);
							boolean endEntity = isEndEntity(chunkTailer);
							ByteBuffer buffer = ByteBuffer.allocate(chunkTailer.length());
							buffer.put(chunkTailer.getArray());
							processed += chunkTailer.length();
							to.write(buffer);
							if(endEntity) {
								complete = true;
							}
						}
			} catch(MessageHeaderException mhe) {
				if(processed == 0) {
					throw new EntityException("Entity Input closed. isChunk="+isChunk,mhe);
				} else {
					// NOTE: chunk finishes - not formally specified in HTTP 1.1 specification
					// but used by some web application.
					complete = true;
				}
			} catch(IOException e) {
				throw new EntityException("Entity Error. isChunk="+isChunk,e);
			}
		}
		return entitySize;
	}
	private boolean isEndEntity(ByteArray buffer) {
		return buffer.get(0)==ByteArray.CR && buffer.get(1)== ByteArray.LF;
	}
	public ByteArray getLine(SocketChannel socketChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1);
		boolean validHeader = false;
		ByteArray byteArray = new ByteArray();
		// read one byte 
		int i = 0;
		try {
		while((i=socketChannel.read(buffer)) != -1) {
			if(i == 0) {
				System.err.println(getClass().getName()+": read 0 byte from buffer");
				continue;
			}
			buffer.flip();
			byte data = buffer.get();
			byteArray.append(data);
			if(byteArray.length() < 2) {
				// clear buffer for next read
				buffer.clear();
				continue;
			}
			// start to compare the last two bytes
			int offset = byteArray.length()-2;
			byte[] last4bytes = byteArray.subArray(offset);
			boolean endHttpRequest = true;
			for(int k=0; k<2; k++) {
				if(last4bytes[k] != CRLF[k]) {
					// the last four bytes are not CRLECRLE
					endHttpRequest = false;
					// break for loop
					break;
				}
			}
			if(!endHttpRequest) {
				buffer.clear();
			} else {
				validHeader = true;
				// break while loop
				break;
			}
		}
		logger.debug("##### "+byteArray.length());
		if(validHeader) {
			return byteArray;
		} else {
			if(byteArray.length() == 0) {
				throw new MessageHeaderException("Zero bytes in socket channel when passing Http message header", socketChannel);
			} else {
				throw new MessageHeaderException("Invalid SocketChannel data for Message Header: "+byteArray, socketChannel);
			}
		}
		} catch(IOException e) {
			throw new MessageHeaderException("Error when processing Message Header. ", e, socketChannel);			
		}
	}

	public long getChunkSize(ByteArray entityHead) {
		if(entityHead.length() == 2) {
			// See note in getEntityLine() method.
			return 0L;
		}
		StringBuffer sb = new StringBuffer();
		int size = entityHead.length()-2;
		int index = entityHead.indexOf(';');
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

}
