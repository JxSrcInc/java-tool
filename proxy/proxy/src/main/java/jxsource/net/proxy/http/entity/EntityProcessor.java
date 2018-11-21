package jxsource.net.proxy.http.entity;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpMessage;

public interface EntityProcessor {
	final byte[] CRLF = new byte[] {13,10};
	public final static String EntityComplete = "Complete";
	public final static String EntityTimeOut = "TimeOut";
	public final static String EntityClosed = "Closed";
	public final static String NoEntity = "NoEntity";
	public EntityStatus processEntity(HttpMessage message, InputStream from, EntityDestinationOutputStream to) throws IOException;
}
