package jxsource.net.proxy.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import jxsource.net.proxy.ChannelController;
import jxsource.net.proxy.ChannelManager;
import jxsource.net.proxy.Controller;
import jxsource.net.proxy.ControllerManager;
import jxsource.net.proxy.Executor;
import jxsource.net.proxy.SocketChannelUtils;
import jxsource.net.proxy.UrlInfo;
import jxsource.net.proxy.Utils;
import jxsource.net.proxy.http.entity.BlockingEntityProcessor;
import jxsource.net.proxy.http.entity.DefaultDestinationChannel;
import jxsource.net.proxy.http.entity.DownloadDestinationChannel;
import jxsource.net.proxy.http.entity.EntityDestinationChannel;
import jxsource.net.proxy.http.entity.EntityDestinationChannelManager;
import jxsource.net.proxy.http.entity.EntityProcessor;
import jxsource.net.proxy.http.entity.EntityStatus;
import jxsource.net.proxy.http.entity.NoneBlockingEntityProcessor;
import jxsource.net.proxy.http.exception.MessageHeaderException;
import jxsource.util.bytearray.ByteArray;
import jxsource.util.string.StringUtils;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

public class HttpExecutor implements Executor {
	private Logger logger = Logger.getLogger(HttpExecutor.class);

	private SocketChannelUtils channelUtils = new SocketChannelUtils();
	private HttpUtils httpUtils = new HttpUtils();
	private HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	private HttpRequest request;
	private HttpResponse response;
	private SocketChannel localChannel;
	private SocketChannel remoteChannel;
	private RemoteRequest remoteRequest;
	private UrlInfo urlInfo;
	private EntityProcessor entityProcessor = new BlockingEntityProcessor();
	private Exception catchedException;
	private ControllerManager controllerManager;
	private Controller controller;
	private EntityDestinationChannelManager entityDestinationChannelManager;
	private ByteBuffer requestBody;
	private String state = E_Wait;
	private Utils utils = new Utils();
	// Configuration
	public void setEntityDestinationChannelManager(EntityDestinationChannelManager entityDestinationChannelManager) {
		this.entityDestinationChannelManager = entityDestinationChannelManager;
	}
	public void setControllerManager(ControllerManager controllerManager) {
		this.controllerManager = controllerManager;
	}

	public void init() {} // not use
	public String getState() {
		return state;
	}
	public ControllerManager getControllerManager() {
		return controllerManager;
	}
	public Exception getCatchedException() {
		return catchedException;
	}
//	public ChannelController getRemoteChannelController() {
//		return controller;
//	}
	public UrlInfo getConnectToUrlInfo() {
		return urlInfo;
	}

	public void reset() {
		request = null;
		response = null;
		requestBody = null;
		remoteRequest = null;
		if(controller != null) {
			controller.releaseSocketChannel();
		}
//		controllerManager.releaseChannelController(controller);
		// controller and remoteChannel must discard
		remoteChannel = null;
		controller = null;
		state = E_Wait;
		catchedException = null;
	}

	public void prepairReconnect() {
		controller.removeSocketChannel();
//		controllerManager.releaseChannelController(controller);
		state = E_LocalRequestRead;
		remoteChannel = null;
		controller = null;
	}
	public void destroy() {
		this.reset();
	}

	public String getMessage() {
		String msg = "Executor: "+state+"\n" + request+ "\n" + response;
		return StringUtils.addTabToLine(msg);
	}

	// called by BridgeServer
	public void setLocalSocketChannel(SocketChannel localChannel) {
		this.localChannel = localChannel;
	}

	// called by WorkThread
	public SocketChannel getLocalChannel() {
		return this.localChannel;
	}

	public String procLocalRequest() {
		requestBody = null;
		try {
			if (remoteRequest == null) {
				remoteRequest = httpUtils.createRemoteRequest(localChannel);
			}
			urlInfo = remoteRequest.getUrlInfo();
			request = remoteRequest.getRequest();
			logger.debug(request);
			headerUtils.setHttpMessage(request);
			String method = request.getRequestLine().getMethod().toUpperCase();
			if(method.equals("GET")) {
				// continue
			} else 
			if(method.equals("POST")) {
				String contentLength = headerUtils.getHeaderAndValue("Content-Length");
				if(contentLength == null) {
					System.err.println("POST request without Content-Length");
					System.exit(1);
				}
				int length = Integer.parseInt(contentLength);
				requestBody = ByteBuffer.allocate(length);

				int processed = 0;
				while(processed < length) {
					processed += localChannel.read(requestBody);
				}
			} else {
				System.err.println("Un-handled method: "+method);
				System.exit(1);
			}
			return state = E_LocalRequestRead;
		} catch(Exception e) {
			catchedException = e;
//			logger.error("Fail to read local request", e);
			return state = E_Error;
		}
	}
	/*
	 * returns a not-connected remote channel
	 */
	public SocketChannel getRemoteChannel() {

			controller = controllerManager.getController(urlInfo);
//			logger.debug("getRemoteChannel: controller "+controller);
			try {
				remoteChannel = controller.getSocketChannel();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return remoteChannel;

	}

	public String procRemoteRequest() {
		try {
			logger.debug("" + request);
			httpUtils.writeRequest(request, remoteChannel);
			if(requestBody != null) {
				requestBody.flip();
//				remoteChannel.write(requestBody);
				utils.transfer(requestBody, remoteChannel);
			}
			return state = E_RemoteRequestWritten;
		} catch (Exception e) {
			// TODO: reconnect?
			catchedException = e;
			return state = E_RemoteChannelError;
		}
	}

	public String procRemoteResponse() {
		try {
			// Read response message head from target channel
			ByteArray head = httpUtils.getMessageHead(remoteChannel);
//			if (head.length() == 0) {
//				return state = E_RemoteChannelError;
//			}
			state = this.E_RemoteResponseRead;
			response = httpUtils.getHttpResponse(head);
			logger.debug("" + response);
			// write response message head to src channel
			channelUtils.writeByteArrayToChannel(head.getArray(), localChannel);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 200 && statusCode != 304 && statusCode != 204) {

				// process response message body
//				EntityDestinationChannel edc = new DownloadDestinationChannel();
//				EntityDestinationChannel edc = new DefaultDestinationChannel();
				EntityDestinationChannel edc = entityDestinationChannelManager.getEntityDestinationChannel(request, response);
				edc.setSocketChannel(localChannel);
				EntityStatus entityStatus = entityProcessor.processEntity(
						response, remoteChannel, edc);
				logger.debug("" + entityStatus);
			}
//			reset();
			return state = this.E_TransComplete;
		} catch (MessageHeaderException e) {
			// fail to process response header
			catchedException = e;
			return state = E_RemoteChannelError;

		} catch (Exception e) {
			catchedException = e;
			if(state == E_RemoteResponseRead) {
				// may remote read error or local write error
				return state = E_Error;
			} else {
				// duplcate catch as MessageHeaderException
				// TODO: remove one
				return state = E_RemoteChannelError;
			}
		}

	}

}
