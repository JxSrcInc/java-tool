package jxsource.net.proxy.test.http;

import java.io.OutputStream;

public interface EntityResponse {
	public void write(OutputStream out) throws Exception;
}
