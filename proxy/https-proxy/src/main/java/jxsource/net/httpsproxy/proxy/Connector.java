package jxsource.net.httpsproxy.proxy;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.http.HttpRequest;
import org.apache.log4j.Logger;

import jxsource.net.proxy.UrlInfo;
import jxsource.util.buffer.bytebuffer.ByteArray;

public class Connector {
	private static Logger logger = Logger.getLogger(Connector.class);
	private SocketChannel localChannel;
	HttpUtils httpUtils = new HttpUtils();
	public void setLocalSocketChannel(SocketChannel localChannel) {
		this.localChannel = localChannel;
	}

	public SocketChannel getLocalSocketChannel() {
		return localChannel;
	}

//	public SocketChannel getRemoteSocketChannel(SocketChannel channel) {
//		try {
//			HttpRequest request = httpUtils.getHttpRequest(channel);
	public SocketChannel getRemoteSocketChannel(byte[] data) {
		try {
			ByteArray ba = new ByteArray();
			ba.append(data);
			HttpRequest request = httpUtils.getHttpRequest(ba);
			logger.debug(request);
			String host = request.getFirstHeader("Host").getValue();
			int port = 443;
			int index = host.indexOf(':');
			if(index > 0) {
				port = Integer.parseInt(host.substring(index+1));
				host = host.substring(0, index);
			}
			InetSocketAddress isa = new InetSocketAddress(host,port);
			SocketChannel remoteChannel = null;
			try {
				remoteChannel = SocketChannel.open();
				remoteChannel.connect(isa);
			} catch(Exception e) {
				logger.error("remote connection error",e);
				remoteChannel = SocketChannel.open();
				remoteChannel.connect(isa);				
			}
			
			return remoteChannel;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
