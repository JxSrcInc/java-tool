package jxsource.net.httpsproxy.bridge;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.http.HttpRequest;
import org.apache.log4j.Logger;

import jxsource.net.httpsproxy.proxy.Connector;
import jxsource.net.httpsproxy.proxy.HttpUtils;
import jxsource.net.proxy.UrlInfo;
import jxsource.util.buffer.bytebuffer.ByteArray;

public class BridgeConnector extends Connector {
	private static Logger logger = Logger.getLogger(BridgeConnector.class);
	private SocketChannel localChannel;
	HttpUtils httpUtils = new HttpUtils();
	public void setLocalSocketChannel(SocketChannel localChannel) {
		this.localChannel = localChannel;
	}

	public SocketChannel getLocalSocketChannel() {
		return localChannel;
	}

	public SocketChannel getRemoteSocketChannel(byte[] data) {
		try {
			// proxy server which uses local SSL key
			InetSocketAddress isa = new InetSocketAddress("localhost",10090);
			
			// if remote host is in remoteHost.txt file
			// then set remote host to it instead of using proxy server
			ByteArray ba = new ByteArray();
			ba.append(data);
			HttpRequest request = httpUtils.getHttpRequest(ba);
			logger.debug(request);
			String hostName = request.getFirstHeader("Host").getValue();
			int port = 443;
			int index = hostName.indexOf(':');
			if(index > 0) {
				String portStr = hostName.substring(index+1);
				port = Integer.parseInt(portStr);
				hostName = hostName.substring(0, index);
				String host = hostName+':'+portStr;
				if(HostManager.getInstance().contains(host)) {
					isa = new InetSocketAddress(hostName,port);
				}
			}
//			if(hostName.equals("sitecatalyst.fidelity.com")) {
//				isa = new InetSocketAddress(hostName,port);
//			System.err.println("\n"+HostManager.getInstance().contains("sitecatalyst.fidelity.com:443"));
//			}
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
