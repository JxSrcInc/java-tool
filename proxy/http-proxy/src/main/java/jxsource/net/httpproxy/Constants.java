package jxsource.net.httpproxy;

public class Constants {

	public static final String defaultConfigFile = "jxsource.net.httpproxy.cfg";
	public static final int AllowedUnknownEntityCopyTime = 1000 * 60;
	public static final int CloseEntityTimeOut = 1000 * 60;
	// buffer size used for write HttpRequest/HttpResponse to OutputStream
	public static final int SessionBufferSize = 1024 * 4;
	// buffer size used for entity of Content-Length
	public static final int EntityBufferSize = 4096 * 8;
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

}
