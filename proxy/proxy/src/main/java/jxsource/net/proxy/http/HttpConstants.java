package jxsource.net.proxy.http;

public class HttpConstants {

	public static final int AllowedUnknownEntityCopyTime = 1000 * 60;
	public static final int CloseEntityTimeOut = 1000 * 60;
	// buffer size used for write HttpRequest/HttpResponse to OutputStream
	public static final int SessionBufferSize = 1024 * 4;
	// buffer size used for entity of Content-Length
	public static final int EntityBufferSize = 4096 * 8;

}
