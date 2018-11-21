package jxsource.net.bridge.http;

// http(:|%3A)//bridge__...__bridge -> https://host[:port]
public class HttpBridgeToRemoteHttpsConverter extends BridgeToRemoteHttpsConverter{

	@Override
	public String getRegex() {
		// TODO Auto-generated method stub
		return "http(:|%3A)//"+super.getRegex();
	}


}
