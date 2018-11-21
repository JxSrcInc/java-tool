package jxsource.net.http.xpath.pageupdate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Vector;

import jxsource.util.io.FileUtil;
import jxsource.xml.dom.Document;
import jxsource.xml.dom.DocumentBuilder;
import jxsource.xml.dom.Element;
import jxsource.xml.dom.Node;
import jxsource.xml.dom.NodeOperator;
import jxsource.xml.dom.TreeProcessor;
import jxsource.xml.io.XmlOutputStream;
import jxsource.xml.parser.Parser;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;

/*
 * HtmlDocModifier modifies 
 * 	1. src attribute in <img>, <script>, <frame>, <iframe>, .....
 * 	2. href attribute in <a>, <link>, .....
 * 	3. action attribute in <form>, .....
 * in such a way
 * 	1. create Link using attribute value and active RequestInfo to destination
 * 	2. add Link to LinkManager.
 * 	3. remove protocol/schema and host/authority from attribute if they exist.
 * 	4. add RedirectConstants.INTERCEPTER_BASE before path, so IntercepterHandler can handle it.
 * 	
 */
public class HtmlDocModifier extends TreeProcessor {
	private static Logger logger = Logger.getLogger(HtmlDocModifier.class);
	XmlOutputStream writer;
	boolean updated;
	String headId = "0";
	
	private String[] updateElements = new String[] { "td", "th", "div", "a", "span",
			"dd", "img", "p" };

	public HtmlDocModifier() {
		this.addProcessor(new NodeLinkModifier());
	}

	public boolean isModified() {
		return updated;
	}

	private boolean isUpdateElement(String name) {
		for (String s : updateElements) {
			if (s.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private void addEventHandler(Element ele) {
		if (isUpdateElement(ele.getName())
				&& ele.getAttribute("attrPath") == null) {
			// attrPath attribute has the value of XPath of the node
			ele.setAttribute("attrPath", ele.getRootPath());
			ele.setAttribute("onmousemove", "setLeafBackgroundColor(this)");
			ele.setAttribute("ondblclick", "mouseClick(this)");
			updated = true;
		} else if (ele.getName().equals("body")) {
			ele.setAttribute("onkeypress", "keypress(event)");
		} else if (ele.getName().equals("head")) {
			try {
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				writer.write(buf, ele);
				buf.close();
				headId = LinkManager.getInstance().createLink(buf.toByteArray());
			} catch(IOException ioe) {
				logger.error("error when extracting head:", ioe);
			}
		}
	}

	class NodeLinkModifier implements NodeOperator {
		public boolean proc(Node node) {
			if (node instanceof Element) {
				Element ele = (Element) node;
				addEventHandler(ele);
			}
			return true;
		}
	}
	public static void main(String[] args) {
		try {
			java.io.InputStream in = new java.io.FileInputStream("C:\\dev\\eclipse\\httpproxy\\http-xpath\\t.html");
			Document doc = new DocumentBuilder().buildeDoc(new Parser(in, "UTF-8"));
//			System.out.println(new jxsource.xml.io.XmlOutputStream().getString(doc));

		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	private Document createDocument(TextEntityString textEntityString)
			throws IOException {
		InputStream in = new ByteArrayInputStream(
				textEntityString.getEntityBytes());
		Charset charset = textEntityString.getCharset();
		updated = false;
		Document doc = new DocumentBuilder().buildeDoc(new Parser(in, charset));
		this.runAll(doc);
//		System.out.println(new jxsource.xml.io.XmlOutputStream().getString(doc));
		return doc;
	}

	public byte[] modify(TextEntityString textEntityString,
			HttpResponse response) {
		String raw = new String(textEntityString.getEntityBytes());
		updated = false;
		if (raw.indexOf("<html") != -1 || raw.indexOf("html>") != -1) {
			// it is html file, try to add xpath
			try {
				// Convert textEntityString to a new Document
				// and modify all Elements which are in "updateElements" array
				Charset charset = textEntityString.getCharset();
				writer = new XmlOutputStream(charset);

				Document doc = this.createDocument(textEntityString);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				writer.write(out, doc);
				// updated value is setup by createDocument() method.
				if (updated) {
					// xpath added to original html file
					// so add javascript and action html
					byte[] b = out.toByteArray();
					return addScripts(b, doc, charset, response);
				} else {
					// return original html
					return textEntityString.getEntityBytes();
				}
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
				updated = false;
			}
		}
			// no update
			return textEntityString.getEntityBytes();
		
	}

	private int getIndexOf(String src, String search) {
		int i = src.indexOf(search);
		if(i == -1) {
			i = src.indexOf(search.toUpperCase());
		}
		if(i == -1) {
			i = src.indexOf(search.toLowerCase());
		}
		return i;
	}
	private byte[] addScripts(byte[] entity, Document doc, Charset charset,
			HttpResponse response) throws IOException {
		String target = new String(entity, charset);
		// create javascript using UTF-8 encoding 
		String headScript = "<script type=\"text/javascript\">"
				+ new String(
						FileUtil.loadByteArray(Constants.XPath_Js),
						Charset.forName("UTF-8"))
				+ new String(FileUtil.loadByteArray(Constants.Lib_Js),
						Charset.forName("UTF-8")) + "</script>";
		// ___HEADID___ is defined in lib.js
		// Now replace it with a unique id which identifies the current html page.
		headScript = headScript.replace("___HEADID___", this.headId);
		// insert script before </head> tag
		int pos = getIndexOf(target, "</head>");
		if (pos != -1) {
			target = target.substring(0, pos) + headScript
					+ target.substring(pos);
		}
		// add headHtml.html at the end of html body
		Vector<Node> nodes = doc.getNodesByName("body");
		if (nodes.size() > 0) {
			pos = getIndexOf(target,"</body>");
			if (pos != -1) {
				String headHtml = new String(
						FileUtil.loadByteArray(Constants.HeadHtml_Html),
						Charset.forName("UTF-8"));
				target = target.substring(0, pos) + headHtml
						+ target.substring(pos);
			}
		}
		// create response entity.
		byte[] newEntityBytes = target.getBytes(charset);
		if (response.getFirstHeader("Content-Length") != null) {
			response.setHeader("Content-Length",
					Integer.toString(newEntityBytes.length));
		} else {
			response.addHeader("Content-Length",
					Integer.toString(newEntityBytes.length));
		}
		return newEntityBytes;
	}

}
