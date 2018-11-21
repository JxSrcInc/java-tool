package jxsource.net.bridge.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxsource.net.proxy.exception.ClientCloseException;
import jxsource.net.proxy.http.HttpHeaderUtils;
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
public class HttpBridgeRequestHandler extends HttpEntityModifyRequestHandler {

	protected static Logger logger = Logger
			.getLogger(HttpBridgeRequestHandler.class);
	private String prefixBridge = BridgeContext.prefixBridge;
	private String suffixBridge = BridgeContext.suffixBridge;
	private HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	private boolean updateEntityWithBridgeContext;
	private BridgeUtils bridgeUtils = new BridgeUtils();

	// 1. create BridgeContext to keep fault url in incoming http call 
	// 2. create real UrlInfo
	// 3. create HttpRequest with real requestLine but without changes 
	// 	  for any header -- header change will be handled separatly.

	// super class uses urlInfo to connect to remote server
	// however workThread passes fault remote url in urlInfo
	// so urlInfo must be updated and put in RequestInfo
	private RequestInfo updateRequestInfo(RequestInfo requestInfo) {
		BridgeContext bridgeContext = BridgeContextHolder.get();
		if (bridgeContext != null) {
			// create new UrlInfo;
			urlInfo = requestInfo.getUrlInfo().clone();
			String bridgeHostname = urlInfo.getHostname();
			logger.debug("bridgeContext: " + bridgeContext);
			urlInfo.setPort(bridgeContext.getRemotePort());
			urlInfo.setHostname(bridgeContext.getRemoteHostname());
			urlInfo.setProtocol(bridgeContext.getRemoteProtocol());
			urlInfo.setPath(bridgeUtils.bridgeToRemote(urlInfo.getPath()));
		} else {
			// existing UrlInfo
			// fix bug: http call contains http://bridge__ in path
			urlInfo.setPath(bridgeUtils.bridgeToRemoteHostConverter().matchAndReplace(urlInfo.getPath()));
		}

		// create new HttpRequest
		HttpRequest r = requestInfo.getRequest();
		String method = r.getRequestLine().getMethod();
		ProtocolVersion ver = r.getRequestLine().getProtocolVersion();
		// Note: the uri in new request has no authority. its only path
		String path = urlInfo.getPath();
		logger.debug("RemoteRequest URI: "+path);
		request = new BasicHttpRequest(method, path, ver);
		// copy headers
		for (Header header : r.getAllHeaders()) {
			String name = header.getName();
			String value = header.getValue();
			String newValue = value;
			if(name.toUpperCase().equals("HOST")) {
				newValue = bridgeUtils.bridgeToRemoteHostConverter()
						.matchAndReplace(value);
			} else {
				newValue = bridgeUtils.bridgeToRemote(value);				
			}
			request.addHeader(name, newValue);
			// TODO: cookies?
		}
		requestInfo.setUrlInfo(urlInfo);
		requestInfo.setRequest(request);
		// Added remote request to BridgeContext
		if(bridgeContext != null) {
			bridgeContext.setRemoteRequest(request);
		}
		return requestInfo;

	}


	/*
	 * Convert bridge__....__bridge to remote protocol://host[:port] Possible
	 * fields: Host, Reference, Location
	 */
	@Override
	public String procLocalRequest() {
		try {

			if (requestInfo == null) {
				requestInfo = httpUtils.createRequestInfo(localInputStream);
			}
			// if url has format bridge__...__brideg
			// then update requestInfo and set flag to modify response later
			if (requestInfo.getUrlInfo().getHostName().indexOf(prefixBridge) >= 0
					&& requestInfo.getUrlInfo().getHostName()
							.indexOf(suffixBridge) >= 0) {
				// Browser makes a fault http call in format:
				// http://bridge__<bridge-context>__bridge/<path>
				// where the real authority in the real http call is embed in
				// <bridge-context>.
				// Example:
				// Real http call: https://www.abc.com:8759/path
				// Fault http call:
				// http://bridge__https__host__www.abc.com__port__8759__bridge
				//
				// See updateBridgeContext javadoc for what it does

				String bridgeHostname = requestInfo.getUrlInfo().getHostname();
				BridgeContext bridgeContext = new BridgeContext();
				bridgeContext.setBridgeHost(bridgeHostname);
				logger.debug("setBridgeContext: "+bridgeContext);
				BridgeContextHolder.setBridgeContext(bridgeContext);
				updateEntityWithBridgeContext = true;
			} else {
				logger.debug("setBridgeContext: null");
				BridgeContextHolder.setBridgeContext(null);
				updateEntityWithBridgeContext = false;
			}

			// In a none bridge http request, 
			// the path and/or other headers may contains "bridge__...__bridge"
			// so all requestInfo must update
			requestInfo = updateRequestInfo(requestInfo);
			// TODO: show 
logger.debug("request: "+requestInfo.getRequest());
logger.debug("urlInfo: "+requestInfo.getUrlInfo());
logger.debug("updateEntityWithBridebContext: "+updateEntityWithBridgeContext);

			return super.procLocalRequest();
		} catch (MessageHeaderException e) {
			catchedException = new ClientCloseException(e);
			return state = E_Error;
		} catch (Exception e) {
			catchedException = e;
			return state = E_Error;
		}
	}

	/*
	 * Replace all protocol://host[:port] and host[:port] within response
	 * headers and
	 * 
	 * @see
	 * jxsource.net.bridge.http.HttpEntityModifyRequestHandler#procRemoteResponse
	 * ()
	 */
	@Override
	public String procRemoteResponse() {
		// pass BridgeContext to entityModifier
		((HttpsBridgeEntityModifier) entityModifier)
				.setBridgeContext(BridgeContextHolder.get());

		// call super class operation
		return super.procRemoteResponse();
	}

	/*
	 * Override HttpEntityModifyRequestHandler.updateResponse() method to
	 * replace protocol://host[:port] and host[:port] with saved
	 * bridge__...__brideg in message content
	 */
	@Override
	protected void updateResponse(HttpResponse response) {
		logger.debug("Server Response: "+response);
		/*
		 * We cannot use BridgeContextHolder.get()==null in the if condition
		 * below because we don't need to update response entity if request's
		 * uri does not contain "bridge__ .... __bridge"
		 */
		if (!updateEntityWithBridgeContext) {
			return;
		}
		BridgeContext bridgeContext = BridgeContextHolder.get();
		updateHeaders(response, bridgeContext.getRemoteHost(),
				bridgeContext.getBridgeHost());
	}

	private void updateHeaders(HttpMessage message, String oldValue,
			String newValue) {
		// TODO: modify response header
		List<Header> toChangeHeader = new ArrayList<Header>();
		for (Header header : message.getAllHeaders()) {
			if (header.getValue().indexOf(oldValue) != -1) {
				logger.debug("Replace Header: " + header.getName() + "="
						+ header.getValue() + ", " + oldValue);
				toChangeHeader.add(header);
			}
		}
		for (Header h : toChangeHeader) {
			String value = h.getValue().replaceAll(oldValue, newValue);
			message.removeHeader(h);
			System.err.println(Thread.currentThread().getName() + ": "
					+ getClass().getName() + "- " + h.getName() + '=' + value);
			message.addHeader(h.getName(), value);
		}
		logger.debug(message);

	}
}
