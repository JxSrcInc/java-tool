package jxsource.net.proxy.http.entity;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface EntityDestinationChannelManager {
	public EntityDestinationChannel getEntityDestinationChannel(
			HttpRequest request, HttpResponse response);
}
