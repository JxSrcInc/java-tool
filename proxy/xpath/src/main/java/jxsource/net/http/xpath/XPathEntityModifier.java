package jxsource.net.http.xpath;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import jxsource.net.http.xpath.pageupdate.HtmlDocModifier;
import jxsource.net.http.xpath.pageupdate.TextEntityString;
import jxsource.net.http.xpath.pageupdate.TextEntityStringFactory;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.entity.EntityModifier;

public class XPathEntityModifier extends EntityModifier{
	private static Logger logger = Logger.getLogger(XPathEntityModifier.class);
	private HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	private TextEntityStringFactory tesFactory = new TextEntityStringFactory();
	private HtmlDocModifier htmlDocModifier = new HtmlDocModifier();
	public XPathEntityModifier() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] updateEntity(byte[] entityBytes, HttpResponse response) {
		headerUtils.setHttpMessage(response);
		ContentType contentType = headerUtils.getContentType();
		// Is response's contentType appliable?
		// Determine the Charset (or unspecified) and 
		// create the String value of the entityData.
		TextEntityString entityString = tesFactory.create(
				contentType.getCharset(), entityBytes);
//		boolean updated = false;
		if(entityString.isValid()) {
			// Update contentType
			contentType = ContentType.create(
					contentType.getMimeType(),
					entityString.getCharset());

			// update entity
			entityBytes = htmlDocModifier.modify(entityString, response);

//System.err.println(htmlDocModifier.isModified());
		} else {
			logger.error("Invalid entity string: "+entityString);
		}
		return entityBytes;
	}

}
