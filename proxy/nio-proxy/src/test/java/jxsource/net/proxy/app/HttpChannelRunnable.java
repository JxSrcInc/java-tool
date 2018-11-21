package jxsource.net.proxy.app;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import jxsource.net.proxy.ChannelController;
import jxsource.net.proxy.UrlInfo;
import jxsource.net.proxy.Utils;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.HttpUtils;
import jxsource.net.proxy.http.entity.EntityDestinationChannel;
import jxsource.net.proxy.http.exception.EntityException;

public class HttpChannelRunnable extends ChannelRunnable {
	protected Selector selector;
	protected SelectionKey channelKey;
	protected HttpUtils httpUtils = new HttpUtils();
	protected HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	protected String[] urls;
	ByteArrayOutputStream out;
	String proxyHostname;
	int proxyPort;
	long processed = 0;
	
	public HttpChannelRunnable(ChannelTestManager mgr, String[] urls) {
		super(mgr);
		logger = Logger.getLogger(HttpChannelRunnable.class);
		this.urls = urls;
		try {
			selector = Selector.open();
		} catch (IOException e) {
			throw new RuntimeException("Fail to create HttpChannelRunnable.", e);
		}
	}

	public void setProxy(String proxyHostname, int proxyPort) {
		this.proxyHostname = proxyHostname;
		this.proxyPort = proxyPort;
	}
	@Override
	public void run() {
		for (int i = 0; i < urls.length; i++) {
			transaction(urls[i]);
			if(i < urls.length-1) mgr.sleep();
		}
	}
	
	protected void transaction(String url) {
		try {
			UrlInfo urlInfo = new UrlInfo(url);
			if(proxyHostname == null) {
				httptransaction(urlInfo, urlInfo.getHostName(),urlInfo.getPort());
			} else {
				httptransaction(urlInfo, proxyHostname, proxyPort);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	protected void httptransaction(UrlInfo urlInfo, String hostname, int port) throws Exception {
		ChannelController controller = mgr.getChannelManager()
				.getChannelController(urlInfo);
		if (controller != mgr.getWorkingController()) {
			mgr.setWorkingController(controller);
		}
		logger.debug("get controller: " + controller.hashCode());
		assertTrue("different controller",
				mgr.getWorkingController() == controller);
		SocketChannel channel = mgr.getChannelManager().getChannel(controller);
		if (channel.isBlocking()) {
			channel.configureBlocking(false);
		}
		if (channel != mgr.getWorkingChannel()) {
			mgr.setWorkingChannel(channel);
		}
		logger.debug("get channel: " + channel.hashCode());
		assertTrue("different channel", mgr.getWorkingChannel() == channel);
		if (channel.isConnected()) {
			logger.debug("Connected: "+channel);
			channelKey = channel.register(selector, SelectionKey.OP_WRITE);
		} else {
			logger.debug("Connect to "+hostname+":"+port);
			channelKey = channel.register(selector, SelectionKey.OP_CONNECT);
			InetSocketAddress isa = new InetSocketAddress(hostname, port);
			channel.connect(isa);
		}

		boolean ready = true;
		boolean headerProcessed = false;
		long length = -1;
		try {
			while (ready) {
				int num = selector.select();
				Iterator<SelectionKey> selectionKeys = selector.selectedKeys()
						.iterator();
				while (selectionKeys.hasNext()) {
					SelectionKey key = selectionKeys.next();
					selectionKeys.remove();
					SocketChannel keyChannel = (SocketChannel) key.channel();
					if (key.isConnectable()) {
						if (keyChannel.isConnectionPending()) {
							keyChannel.finishConnect();
							keyChannel.register(selector, SelectionKey.OP_WRITE);
						}
					} else if (key.isWritable()) {
						HttpRequest request = HttpUtils.createHttpRequest(urlInfo.getUri());
						logger.debug(request);
						byte[] requestByte = httpUtils.getRequestBytes(request);
						ByteBuffer buffer = ByteBuffer.allocate(requestByte.length);
						buffer.put(requestByte);
						buffer.flip();
						httpUtils.writeRequest(request, keyChannel);
						keyChannel.register(selector, SelectionKey.OP_READ);
					} else if (key.isReadable()) {
						if(!headerProcessed) {
							HttpResponse response = httpUtils.getHttpResponse(keyChannel);
							logger.debug(response);
							headerUtils.setHttpMessage(response);
							if(headerUtils.hasHeader("Content-Length")) {
								headerProcessed = true;
								length = Long.parseLong(headerUtils.getHeaderAndValue("Content-Length"));
								out = new ByteArrayOutputStream();
								processed = 0;
							} else {
								ready = false;
							}
						} else 
						if(length > 0) {
							long bytes = procLength(length, keyChannel, out);
							if(bytes == length || bytes < 0) {
								out.close();
								logger.debug("\n*****\n"+new String(out.toByteArray()));
								ready = false;
							} else
							if(bytes == 0) {
								Thread.sleep(50);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			Set<SelectionKey> keys = selector.keys();
			mgr.getChannelManager().releaseChannelController(controller);
		}

	}
	public long procLength(long length, SocketChannel from,
			OutputStream out) throws IOException {
		InputStream in = from.socket().getInputStream();
		ByteBuffer buffer = ByteBuffer.allocate(1024*10);
		int i = from.read(buffer);
		if (i > 0 && (processed + i) <= length) {
			processed += i;
			byte[] buf = new byte[buffer.position()];
			buffer.flip();
			buffer.get(buf);
			out.write(buf);
			out.flush();
			return processed;
		} else {
			return i;
		}
	}


}
