package jxsource.net.bridge.http;

// bridge__...__bridge -> host[:port]
public class BridgeToRemoteHostConverter implements RegexMatchReplace{

	public String replace(String bridgeHost) {
		BridgeContext bridgeContext = new BridgeContext();
		bridgeContext.setBridgeHost(bridgeHost);
		return bridgeContext.getRemoteHost();
	}
	public String getRegex() {
		return "bridge__.+?__bridge";
	}

}
