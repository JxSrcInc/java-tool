package jxsource.net.proxy;


public class Constants {
	public static final int BufferCapacity = 4096 * 10;
	public static final long InfiniteLong = 0x7FFFFFFFFFFFFFFFL;
	public static final long InfiniteInt = 0x7FFFFFFF;
	public static final int SelectorWaitTime = 1000 * 60;
	public static final String CacheDir = "cacheDir";
	// used by stream which reads bytes and then write them at the same time
	public static final String CacheMimeType = "cacheMimeType";
	// used by stream which reads bytes and save them in a buffer
	// so entity can be modified before sending to client
	public static final String BufferedMimeType = "bufferedMimeType";
	public static final String BridgeDir = "bridgeDir";
	public static final String BridgeMimeType = "bridgeMimeType";
	public static final String defaultAppPropertiesFile = "app.properties";
}
