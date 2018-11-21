package jxsource.net.http.xpath;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.ZipException;

import jxsource.net.http.xpath.pageupdate.HtmlDocModifier;
import jxsource.net.http.xpath.pageupdate.TextEntityString;
import jxsource.net.http.xpath.pageupdate.TextEntityStringFactory;
import jxsource.net.proxy.Controller;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.HttpUtils;
import jxsource.net.proxy.http.MimeType;
import jxsource.net.proxy.http.entity.EntityDestinationOutputStream;
import jxsource.net.proxy.http.exception.EntityException;
import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.io.FileUtil;
import jxsource.util.io.GZIPUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.http.contrib.compress.GzipCompressingEntity;
import org.apache.http.contrib.compress.GzipDecompressingEntity;

public class XPathDestinationOutputStream implements EntityDestinationOutputStream{
	private static Logger logger = Logger.getLogger(XPathDestinationOutputStream.class);
	private OutputStream out;
	private ByteArray entitySrc = new ByteArray();
	private HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	private TextEntityStringFactory tesFactory = new TextEntityStringFactory();
	private HtmlDocModifier entityModifier = new HtmlDocModifier();
	private HttpResponse response;
	private HttpUtils httpUtils = new HttpUtils();
	private GZIPUtil gzipUtil = new GZIPUtil();
	
	public XPathDestinationOutputStream(HttpResponse response) {
		this.response = response;
		headerUtils.setHttpMessage(response);
	}
	
	public OutputStream getOutputStream() {
		return out;
	}
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}
	
	public void write(byte[] buffer) throws IOException {
		// skip chunk information
	}
	
	public void write(byte[] buffer, int offset, int length) throws IOException {
		// skip chunk information
	}
	
	public void writeContent(byte[] buffer) throws IOException {
		entitySrc.append(buffer);
	}
	
	public void writeContent(byte[] buffer, int offset, int length) throws IOException {
		entitySrc.append(buffer, offset, length);
	}


	public void close() {
	
	}
	
	public byte[] updateEntity(HttpResponse response) throws IOException {
		ContentType contentType = headerUtils.getContentType();

		


		// unzip
		String contentEncoding = headerUtils.getHeaderAndValue("Content-Encoding");//.getContentEncoding(response);
		byte[] entityBytes = entitySrc.getArray();
		logger.debug("> "+entityBytes.length);
		boolean gzip = false;
		if("gzip".equals(contentEncoding)) {
			try {
				entityBytes = gzipUtil.ungzip(entityBytes);
				gzip = true;
			} catch(ZipException ze) {
				logger.error("Not GZIP format");
				// skip processing
				return entityBytes;
			}
		}

		// Is response's contentType appliable?
		// Determine the Charset (or unspecified) and 
		// create the String value of the entityData.
		TextEntityString entityString = tesFactory.create(
				contentType.getCharset(), entityBytes);
		boolean updated = false;
		if(entityString.isValid()) {
			// Update contentType
			contentType = ContentType.create(
					contentType.getMimeType(),
					entityString.getCharset());

			// update entity
			entityBytes = entityModifier.modify(entityString, response);

			updated = entityModifier.isModified();
		}

/*		response.removeHeaders("Content-Length");
		response.addHeader("Content-Length", Integer.toString(entityBytes.length));
		response.removeHeaders("Content-Encoding");
		response.removeHeaders("Transfer-Encoding");
*/		

//		if ("gzip".equals(contentEncoding)) {
		if(gzip) {
			entityBytes = gzipUtil.gzip(entityBytes);
		}
		// change chunked to use Content-Length
		response.removeHeaders("Transfer-Encoding");
		response.setHeader("Content-Length", Integer.toString(entityBytes.length));	
		logger.debug("< "+entityBytes.length);
		return entityBytes;

	}

	public void push() {
		try {
			if(response.getStatusLine().getStatusCode() == 200) {
				byte[] entity = updateEntity(response);
				httpUtils.outputResponse(response, out);
				out.write(entity);
			} else {
				httpUtils.outputResponse(response, out);
				out.write(entitySrc.getArray());
			}
			out.flush();
		} catch(Exception e) {
			throw new EntityException("Error when write entity.", e);
		}
	}

}
