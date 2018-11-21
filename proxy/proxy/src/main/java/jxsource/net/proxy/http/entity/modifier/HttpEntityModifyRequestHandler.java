package jxsource.net.proxy.http.entity.modifier;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import jxsource.net.proxy.http.HttpRequestHandler;
import jxsource.net.proxy.http.entity.EntityDestinationOutputStream;
import jxsource.net.proxy.http.entity.EntityModifier;
import jxsource.net.proxy.http.entity.EntityProcessor;
import jxsource.net.proxy.http.entity.EntityStatus;
import jxsource.net.proxy.http.exception.MessageHeaderException;
import jxsource.util.buffer.bytebuffer.ByteArray;

/*
 * This RequestHandler modifies entity before it returns to client.
 * It overrides the procRemoteResponse() of super class 
 * and uses EntityModifier to modify entity before sending response+entity to client
 * 
 * The derived class
 * 1. must implement updateResponse(HttpResponse) method to modify response headers if needed
 * 2. may override procRemoteResponse() method to perform some actions 
 *    and then call super.procRemoteResponse()
 */
public abstract class HttpEntityModifyRequestHandler extends HttpRequestHandler{

	protected static Logger logger = Logger.getLogger(HttpEntityModifyRequestHandler.class);
	protected EntityModifier entityModifier;
	
	public void setEntityModifier(EntityModifier entityModifier) {
		this.entityModifier = entityModifier;
	}
	
	protected abstract void updateResponse(HttpResponse response);
	@Override
	public String procRemoteResponse() {
		try {
			remoteInputStream = remoteSocket.getInputStream();
			// Read response message head from target socket
			ByteArray head = httpUtils.getMessageHead(remoteInputStream);
			state = this.E_RemoteResponseRead;
			response = httpUtils.getHttpResponse(head);
			updateResponse(response);
			logger.debug("" + response);

			int statusCode = response.getStatusLine().getStatusCode();
			boolean remoteConnectionClosedWhenProcessingEntity = false;
			if (statusCode >= 200 && statusCode != 304 && statusCode != 204) {
				// process response message body
				EntityDestinationOutputStream edos = entityDestinationSocketManager.getEntityDestinationSocket(request, response, controller);
				if(edos instanceof BufferedDestinationOutputStream) {
					// modify response entity
					// read entity
					edos.setOutputStream(localOutputStream);
					EntityStatus entityStatus = entityProcessor.processEntity(
							response, remoteInputStream, edos);
					// determine if remote connection closed
					remoteConnectionClosedWhenProcessingEntity = entityStatus.getStatus().equals(EntityProcessor.EntityClosed);
					logger.debug("" + entityStatus);
					ByteArray entitySrc = ((BufferedDestinationOutputStream) edos).getEntity();
					// TODO: MODIFY RESPONSE ENTITY !!!!!
					entityModifier.getModifiedMessageHeaderAndEntity(entitySrc, response);
					entityModifier.writeMessageHeaderAndEntity(localOutputStream);
				} else {
					// send response to client without modification
					httpUtils.outputResponse(response, localOutputStream);
					edos.setOutputStream(localOutputStream);
					EntityStatus entityStatus = entityProcessor.processEntity(
						response, remoteInputStream, edos);
					remoteConnectionClosedWhenProcessingEntity = entityStatus.getStatus().equals(EntityProcessor.EntityClosed);
					logger.debug("" + entityStatus);
				}
				// if Header Connection has value close, close remote connection
				if(remoteConnectionClosedWhenProcessingEntity ||
						headerUtils.hasHeaderWithValue("Connection", "close")	) {
					this.cleanRemoteSocket();
					// Let WorkThread to close
					return E_RemoteConnectionClosed;
				}
			} else {
				// no entity, write response message head to src socket
				socketUtils.writeByteArrayToChannel(head.getArray(), localSocket);
			}
			// NOTE: Don't do reset() here.
			// TransactionThread will do it.
			return state = this.E_TransComplete;
		} catch (MessageHeaderException e) {
			// fail to process response header
			// Let TransactionThread decides weather or not to do a reconnection
			catchedException = e;
			return state = E_RemoteConnectionError;

		} catch (Exception e) {
			catchedException = e;
			if(state == E_RemoteResponseRead) {
				// may remote read error or local write error
				// but part data may send client so 
				return state = E_Error;
			} else {
				// duplcate catch as MessageHeaderException
				// Let TransactionThread decides weather or not to do a reconnection
				// TODO: remove one
				return state = E_RemoteConnectionError;
			}
		}

	}


}
