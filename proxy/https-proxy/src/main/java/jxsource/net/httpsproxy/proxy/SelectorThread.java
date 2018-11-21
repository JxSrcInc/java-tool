package jxsource.net.httpsproxy.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import jxsource.util.buffer.bytebuffer.ByteArray;

import org.apache.log4j.Logger;

public class SelectorThread implements Runnable {
	private final String Local = "Local";
	private final String Remote = "Remote";
	ByteBuffer buffer = ByteBuffer.allocate(1024*10);
	
	private Logger logger = Logger.getLogger(SelectorThread.class);
	private Connector connector;
	private SocketChannel localChannel;
	private SocketChannel remoteChannel;
	private Selector selector;
	private boolean connected;
	private boolean remoteChannelReadReady = false;
	long srcProcessedBytes;
	long dstProcessedBytes;
	// BridgeServer call this method to set Executor before start work thread
	public void setLocalChannel(SocketChannel localChannel) {
		this.localChannel = localChannel;
	}
	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	private boolean init() {
					try {
//						localChannel.configureBlocking(false);
						selector = Selector.open();
						SelectionKey key = localChannel.register(selector, SelectionKey.OP_READ);
						key.attach(Local);
//						connector = new Connector();
						connector.setLocalSocketChannel(localChannel);
						return true;
					} catch (IOException e) {
						logger.error("Fail to initialze work thread.",e);
						return false;
					}
	}
	
	private String prefix() {
		return Thread.currentThread().getName()+" [WorkerThread] - ";
	}

	public void run() {
		if(!init()) {
			return;
		}
		boolean ready = true;
		while (ready) {
			try {
				int num = selector.select(50);
				Iterator<SelectionKey> selectionKeys = selector.selectedKeys()
						.iterator();
				while (selectionKeys.hasNext()) {
					SelectionKey key = selectionKeys.next();
					selectionKeys.remove();
					if(connected) {
						if (key.isReadable() && Local.equals(key.attachment())) {
							int i = transfer(Local, localChannel, Remote, remoteChannel);
							if(i > 0) {
								srcProcessedBytes += i;
								if(!remoteChannelReadReady) {
									SelectionKey remoteKey = remoteChannel.register(selector, SelectionKey.OP_READ);
									remoteKey.attach(Remote);
									remoteChannelReadReady = true;
								}
							} else 
							if(i < 0){
								throw new ChannelCloseException("local channel close.");
							}
//							System.err.println(i+","+srcProcessedBytes);
						} else 
						if (key.isReadable() && Remote.equals(key.attachment())) {
							int i = transfer(Remote, remoteChannel, Local, localChannel);								
							if(i > 0) {
								dstProcessedBytes += i;
							} else 
							if(i < 0){
								throw new ChannelCloseException("remote channel close.");
							}
//							System.err.println("** "+i+","+dstProcessedBytes);
						} else {
							
						}
					} else {
						if (key.isReadable() && Local.equals(key.attachment())) {
							SocketChannel localChannel = (SocketChannel) key.channel();
							ByteBuffer buffer = ByteBuffer.allocate(1024*100);
							int i = localChannel.read(buffer);
							if(i > 0) {
								byte[] buf = new byte[i];
								buffer.flip();
								buffer.get(buf);
								logger.debug(new String(buf));
								
								remoteChannel = connector.getRemoteSocketChannel(buf);
//								remoteChannel = connector.getRemoteSocketChannel(localChannel);
								while(remoteChannel.isConnectionPending()) {
									if(remoteChannel.finishConnect()) {
										break;
									}
								}
								remoteChannel.configureBlocking(false);
								sendConnectedMessageThroughLocalChannel();
								connected = true;
							} else {
								throw new RuntimeException("localChannel.read returns -1 or 0: "+i);
							}
						} else 
							if (key.isWritable() && Local.equals(key.attachment())) {
								sendConnectedMessageThroughLocalChannel();
								connected = true;
						} else {
							System.err.println("Unknown event: key="+key+", channel="+key.channel());
						}
					}
				} // end while (selectionKeys.hasNext()) 
			} catch(ChannelCloseException e) {
				logger.info(e.getMessage());
				ready = false;
			} catch(Exception e) {
				logger.error("Exception from loop.", e);
				ready = false;
			}
		} // end while ready
		logger.info("Stop. srcProcessedBytes="+srcProcessedBytes+", dstProcessedBytes="+dstProcessedBytes);
		try {
			localChannel.close();
		} catch(Exception e) {}
		try {
			remoteChannel.close();
		} catch(Exception e) {}
	}
	
	private int transfer(String srcName, SocketChannel src, String dstName, SocketChannel dst) throws TransferException {
		int i = -1;
		try {
			i = src.read(buffer);
		} catch (IOException e) {
			throw new TransferException(srcName+" channel read error.",e);
		}
		buffer.flip();
		try {
			dst.write(buffer);
			while (buffer.remaining() > 0) {
//				logger.warn(buffer.remaining()+" bytes wait to write.");
				try {
					Thread.sleep(500);
				} catch(InterruptedException ie) {}
				dst.write(buffer);
			}
		} catch (IOException e) {
			throw new TransferException(dstName+" channel write error.",e);
		}
		buffer.clear();
		return i;
	}
	
	private void sendConnectedMessageThroughLocalChannel() throws IOException {
		ByteArray msg = new ByteArray();
		byte CR = 13;
		byte LF = 10;
		byte[] CRLF = new byte[] { CR, LF };
		msg.append("HTTP/1.1 200 Connection established".getBytes());
		msg.append(CRLF);
		msg.append(CRLF);
		ByteBuffer buf = ByteBuffer.allocate(msg.length());
		buf.put(msg.getArray());
		buf.flip();
		localChannel.write(buf);
	}

}
