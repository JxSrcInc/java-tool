package jxsource.net.bridge.http;

public class BridgeContextHolder {

	private static final ThreadLocal<BridgeContext> contextHolder = 
			new ThreadLocal<BridgeContext>();
	
	public static void setBridgeContext(BridgeContext bridgeContext) {
		contextHolder.set(bridgeContext);
	}
	
	public static BridgeContext get() {
		return contextHolder.get();
	}
}
