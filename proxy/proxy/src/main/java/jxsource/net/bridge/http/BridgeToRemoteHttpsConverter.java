package jxsource.net.bridge.http;

//bridge__...__bridge -> https://host[:port]
public class BridgeToRemoteHttpsConverter extends BridgeToRemoteHostConverter{

	@Override
	public String replace(String bridgeHost) {
		return "https://"+super.replace(bridgeHost);
	}

}
