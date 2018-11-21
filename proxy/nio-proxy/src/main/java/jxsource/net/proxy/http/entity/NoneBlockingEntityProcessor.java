package jxsource.net.proxy.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import jxsource.net.proxy.Constants;
import jxsource.net.proxy.DefaultTransferQueue;
import jxsource.net.proxy.TransferQueue;
import jxsource.net.proxy.http.HttpConstants;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.exception.EntityException;
import jxsource.net.proxy.http.exception.MessageHeaderException;
import jxsource.util.bytearray.ByteArray;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.log4j.Logger;

public class NoneBlockingEntityProcessor implements EntityProcessor{
	final byte[] CRLF = ByteArray.CRLF;
	public static final String From = "From";//Worker.Remote;
	public static final String To = "To";//Worker.Local;
	final int InfiniteWait = 0;
	public final static String EntiryComplete = "Complete";
	public final static String EntityTimeOut = "TimeOut";
	public final static String EntityClosed = "Closed";
	public final static String NoEntity = "NoEntity";
	
	private Logger logger = Logger.getLogger(NoneBlockingEntityProcessor.class);
	HttpHeaderUtils headerSearch = new HttpHeaderUtils();
	Selector selector;
	int capacity = HttpConstants.EntityBufferSize;
	int waitTime;
	TransferQueue bufferQueue;

	void setEntityBufferSize(int capacity) {
		this.capacity = capacity;
	}
	public EntityStatus processEntity(HttpMessage message, SocketChannel from, EntityDestinationChannel to) throws IOException {
		// use a new selector for each entity
		selector = Selector.open();		
		// use a new TransferQueue for each entity
		bufferQueue = new DefaultTransferQueue();
		headerSearch.setHttpMessage(message);
		SelectionKey fromKey = from.register(selector, SelectionKey.OP_READ);
		fromKey.attach(From);
		SelectionKey toKey = to.register(selector, SelectionKey.OP_WRITE); 
		toKey.attach(To);

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
					waitTime = HttpConstants.CloseEntityTimeOut;
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
				waitTime = InfiniteWait;
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
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		long processed = 0;
		boolean complete = false;
		while (!complete) {
			try {
				int num = selector.select(waitTime);
				if(num == 0) System.err.println("num="+num);
				Iterator<SelectionKey> selectionKeys = selector.selectedKeys()
						.iterator();
				while (selectionKeys.hasNext()) {
					SelectionKey key = selectionKeys.next();
					selectionKeys.remove();
//					System.err.println(num+","+key.attachment());
					if (key.isReadable() && From.equals(key.attachment())) {
						from = (SocketChannel) key.channel();
						int i = from.read(buffer);
						if (i > 0) {
//							processed += i;
							bufferQueue.add(buffer);
						} else 
						if(i < 0){
//							complete = true;
							System.err.println("****** processed="+processed+", length="+length);
						} else {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
//							System.err.println(i);
						}
					} else 
						if (key.isWritable() && To.equals(key.attachment())) {
							while(!bufferQueue.isEmpty()) {
								buffer = bufferQueue.poll();
								processed += buffer.position();
								to.write(buffer);
								buffer.clear();
								if(processed == length) {
									complete = true;
									bufferQueue.close();
									to.close();
								}
							}
						}					
				}
			} catch(IOException e) {
				throw new EntityException(e);
			}
		}
		if(processed != length) {
			logger.error("***** different content length: processed="+processed+", length="+length);
		}
		return processed;
	}
	class Status {
		public static final int Header = 0;
		public static final int Body = 1;
		public static final int Tailer = 2;
		public static final int LastChunk = 3;
		private int state;
		private boolean complete;
		public String getState() {
			switch(state) {
			case 0:
				return "Header";
			case 1: 
				return "Body";
			case 2:
				return "Tailer";
			case 3:
				return "LastChunk";
				default:
					return "InvalidState";
			}
		}
		public boolean headerWait() {
			return state==Header&&!complete;
		}
		public boolean headerReady() {
			return state==Header&&complete;			
		}
		public boolean bodyWait() {
			return state==Body&&!complete;
		}
		public boolean bodyReady() {
			return state==Body&&complete;			
		}
		public boolean tailerWait() {
			return state==Tailer&&!complete;
		}
		public boolean tailerReady() {
			return state==Tailer&&complete;			
		}
		public boolean lastChunkWait() {
			return state==LastChunk&&!complete;
		}
		public boolean lastChunkReady() {
			return state==LastChunk&&complete;			
		}
		public void changeStatus(int state, boolean complete) {
			this.state = state;
			this.complete = complete;
		}
	}
	long procChunkedEntity(SocketChannel from, EntityDestinationChannel to) {
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		long processedRead = 0;
		long processedReadCount = 0;
		long processedWriteCount = 0;
		long processedFinalCount = 0x7FFFFFFFFFFFFFFFL;
		long chunkSize = 0;
		long entitySize = 0;
		Status status = new Status();
		status.changeStatus(Status.Header, false);
		status.complete = false;
		boolean complete = false;
		Queue<ByteArray> tailerQueue = new LinkedList<ByteArray>();
		int num = 0;
		while (!complete) {
//			System.out.println("loop: -------------------------");
			try {
				num = selector.select(waitTime);
				Iterator<SelectionKey> selectionKeys = selector.selectedKeys()
						.iterator();
				while (selectionKeys.hasNext()) {
					num--;
					SelectionKey key = selectionKeys.next();
					selectionKeys.remove();
//					System.out.println("num="+num+","+key.attachment()+": "+status.getState()+","+status.complete);
					if (key.isReadable() && From.equals(key.attachment())) {
						from = (SocketChannel) key.channel();
						if(status.headerWait()) {
							// proc chunk header
							ByteArray chunkHeader = getLine(from);
							long size = getChunkSize(chunkHeader);
//							System.out.println("read header: chunkzise="+size);
							if(size > 0) {
								chunkSize = size+2; // add CR and LF
								addLineToQueue(chunkHeader);
								status.changeStatus(Status.Header, true);;
								processedRead = 0;
								processedReadCount = 0;
								processedWriteCount = 0;
								processedFinalCount = 0x7FFFFFFFFFFFFFFFL;
								buffer = ByteBuffer.allocate(capacity);
							} else 
							if(size == 0) {
								addLineToQueue(chunkHeader);
								status.changeStatus(Status.LastChunk, true);;								
							}
						} else
						if(status.bodyWait() && processedRead < chunkSize) {
							if(buffer.capacity() > chunkSize-processedRead) {
								// make no more data read in
								buffer = ByteBuffer.allocate((int)(chunkSize-processedRead));
							}
							// chunk body
							int i = from.read(buffer);
//							System.out.println("read chunk: "+i+","+buffer.capacity());
							if (i > 0) {
								if((processedRead + i) <= chunkSize) {
									processedReadCount++;
									processedRead += i;
									bufferQueue.add(buffer);
									if(processedRead == chunkSize) {
										status.changeStatus(Status.Body, true);
										processedFinalCount = processedReadCount;
										entitySize += chunkSize - 2;
									} 
								} else {
									// shouldn't happen
									System.err.println("processedRead + i > chunkSize: "+
										"processedRead="+processedRead+", i="+i+
										", chunkSize="+chunkSize);
								}
							}
						} else 
						if(status.tailerWait()) {
							// end chunk body
							ByteArray chunkTailer = getLine(from);
							boolean endEntity = isEndEntity(chunkTailer);
//							System.out.println("read tailer: "+chunkTailer);
							addLineToQueue(chunkTailer);
							tailerQueue.add(chunkTailer);
							if(endEntity) {
								status.changeStatus(Status.Tailer, true);
							}
						} else {
							// shouldn't happen
							if(num == 0) {
								try {
									// wait write event ready
									System.out.println("Wait 100 ms for write event ready: num="+num+","+key.attachment()+": "+status.getState()+","+status.complete);
									Thread.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								System.out.println("write event ready.");								
							}
						}
					} else 
						if (key.isWritable() && To.equals(key.attachment()) &&
							!bufferQueue.isEmpty()) {
								ByteBuffer wrtieBuffer = bufferQueue.poll();
								if(status.headerReady()) {
									// chunk header
									to.write(wrtieBuffer);
									wrtieBuffer.clear();
									status.changeStatus(Status.Body, false);
								} else 
								if(status.bodyWait() || 
										(status.bodyReady() && processedWriteCount < processedFinalCount)) {
									// body processing
									to.write(wrtieBuffer);
									wrtieBuffer.clear();
									processedWriteCount++;
									if(processedWriteCount == processedFinalCount) {
										// end bocy process. 
										// start next chunk processing loop
										status.changeStatus(Status.Header, false);
									}
								} else
								if(status.lastChunkReady()) {
									if(isEndEntity(wrtieBuffer)) {
										// special case: CRLF follows directly chunk body
										// to indicate entity end.
										complete = true;
									} else {
										status.changeStatus(Status.Tailer, false);
									}
									to.write(wrtieBuffer);
									wrtieBuffer.clear();
								} else
								if(status.tailerWait()) {
									// tailer
									to.write(wrtieBuffer);
									wrtieBuffer.clear();
									processedWriteCount++;
								} else
								if(status.tailerReady()) {
									// tailer
									boolean endEntity = isEndEntity(wrtieBuffer);
									to.write(wrtieBuffer);
									wrtieBuffer.clear();
									processedWriteCount++;
									ByteArray tailer = tailerQueue.poll();
									if(endEntity) {
										complete = true;
										bufferQueue.close();
										to.close();
									}
								}
							
						} else {
							// unexpected event
							try {
								// wait write event ready
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					
					} // while loop for key events
			} catch(IOException e) {
				throw new EntityException(e);
			}
		}
		return entitySize;
	}
	private boolean isEndEntity(ByteBuffer buffer) {
		return buffer.get(0)==ByteArray.CR && buffer.get(1)== ByteArray.LF;
	}
	private boolean isEndEntity(ByteArray buffer) {
		return buffer.get(0)==ByteArray.CR && buffer.get(1)== ByteArray.LF;
	}
	private void addLineToQueue(ByteArray line) {
		ByteBuffer buffer = ByteBuffer.allocate(line.length());
		byte[] buf = line.getArray();
		buffer.put(buf);
		bufferQueue.add(buffer);
		
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
