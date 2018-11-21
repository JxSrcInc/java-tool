package jxsource.net.httpproxy.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipException;

import jxsource.net.httpproxy.HttpHeaderUtils;
import jxsource.net.httpproxy.HttpUtils;
import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.io.GZIPUtil;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

public abstract class EntityModifier {
	private static Logger logger = Logger.getLogger(EntityModifier.class);
	private GZIPUtil gzipUtil = new GZIPUtil();
	private HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	private HttpUtils httpUtils = new HttpUtils();
	private HttpResponse response;
	private byte[] entityBytes;
//	private Map<String, Object> handlerContext;

	protected abstract byte[] updateEntity(byte[] src, HttpResponse response);

//	public void setHandlerContext(Map<String, Object> handlerContext) {
//		this.handlerContext = handlerContext;
//	}
	public byte[] getModifiedMessageHeaderAndEntity(ByteArray entitySrc,
			HttpResponse response) throws IOException, HttpException {
		this.response = response;
		entityBytes = entitySrc.getArray();
		try {
			headerUtils.setHttpMessage(response);
			// unzip
			String contentEncoding = headerUtils
					.getHeaderAndValue("Content-Encoding");// .getContentEncoding(response);
			logger.debug("> " + entityBytes.length);
			boolean gzip = false;
			if ("gzip".equals(contentEncoding)) {
				try {
					entityBytes = gzipUtil.ungzip(entityBytes);
					gzip = true;
				} catch (ZipException ze) {
					logger.debug("Not GZIP format");
					// skip processing
				}
			}

			// TODO: change entityBytes
			entityBytes = updateEntity(entityBytes, response);

			if (gzip) {
				entityBytes = gzipUtil.gzip(entityBytes);
			}
			logger.debug("Modified entity length = " + entityBytes.length);
		} catch (Exception e) {
			logger.error("Fail to modify entity: "+response, e);
			// do nothing. return original entity and let client to handle
			// error.
		}
		return entityBytes;
	}

	/*
	 * This is an utility method to send message header and entity
	 */
	public void writeMessageHeaderAndEntity(OutputStream out)
			throws IOException, HttpException {
		// change chunked to use Content-Length
		response.removeHeaders("Transfer-Encoding");
		response.setHeader("Content-Length",
				Integer.toString(entityBytes.length));
		httpUtils.outputResponse(response, out);
		out.write(entityBytes);
		out.flush();

	}
}
