package jxsource.net.bridge.http;

public class BridgeUtils {
	private RegexMatcher bridgeToRemoteHostConverter;
	private RegexMatcher bridgeToRemoteHttpsConverter;
	private RegexMatcher httpBridgeToRemoteHttpsConverter;
	private RegexMatcher remoteHttpsToBridgeConverter;
	
	public BridgeUtils() {
		bridgeToRemoteHostConverter = new RegexMatcher();
		bridgeToRemoteHostConverter.setReplace(new BridgeToRemoteHostConverter());
		bridgeToRemoteHttpsConverter = new RegexMatcher();
		bridgeToRemoteHttpsConverter.setReplace(new BridgeToRemoteHttpsConverter());
		httpBridgeToRemoteHttpsConverter = new RegexMatcher();
		httpBridgeToRemoteHttpsConverter.setReplace(new HttpBridgeToRemoteHttpsConverter());
		remoteHttpsToBridgeConverter = new RegexMatcher();
		remoteHttpsToBridgeConverter.setReplace(new RemoteHttpsToBridgeConverter());
	}
	
	public RegexMatcher bridgeToRemoteHostConverter() {
		return bridgeToRemoteHostConverter;
	}
	public RegexMatcher bridgeToRemoteHttpsConverter() {
		return bridgeToRemoteHttpsConverter;
	}
	public RegexMatcher httpBridgeToRemoteHttpsConverter() {
		return httpBridgeToRemoteHttpsConverter;
	}
	public RegexMatcher remoteHttpsToBridgeConverter() {
		return remoteHttpsToBridgeConverter;
	}
	public String bridgeToRemote(String src) {
		// convert http://bridge__...__bridge
		src = httpBridgeToRemoteHttpsConverter.matchAndReplace(src);
		// then, convert bridge__...__bridge
		src = bridgeToRemoteHostConverter.matchAndReplace(src);
		return src;
	}

}
