package jxsource.net.bridge.http;

import jxsource.net.proxy.UrlInfo;

// https://host[:port] -> bridge__https__..._bridge
public class RemoteHttpsToBridgeConverter implements RegexMatchReplace{

	public String replace(String remoteHttps) {
		UrlInfo urlInfo = new UrlInfo(remoteHttps);
		if(!"https".equals(urlInfo.getProtocol())) {
			throw new RuntimeException("UrlInfo.protocol is not https: "+urlInfo);
		}
		String bridgeContext = BridgeContext.prefixBridge+
				"https"+BridgeContext.symbolHost+
				urlInfo.getHostname();
		if(urlInfo.getPort() != 443) {
			bridgeContext += BridgeContext.symbolPort+Integer.toString(urlInfo.getPort());
		}
		bridgeContext += BridgeContext.suffixBridge;
		return bridgeContext;
	}
	public String getRegex() {
		return "https://([\\w-]+\\.?)+(:\\d+)*";
	}

}
