package jxsource.net.httpsproxy.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxsource.net.proxy.exception.ClientCloseException;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.HttpRequestHandler;
import jxsource.net.proxy.http.RequestInfo;
import jxsource.net.proxy.http.entity.modifier.HttpEntityModifyRequestHandler;
import jxsource.net.proxy.http.exception.MessageHeaderException;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpRequest;
import org.apache.log4j.Logger;

/*
 * This is special EntityModifyRequestHandler which 
 * decodes "bridge__...__bridge" for local request
 * and encode "bridge__...__bridge" for remote response
 */
/*
 * TODO: it may be better to change to only handle https. because it does not make sense to http.
 */
public class HttpsBridgeRequestHandler extends HttpRequestHandler {

	protected static Logger logger = Logger
			.getLogger(HttpsBridgeRequestHandler.class);
}
