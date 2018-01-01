package jxsource.xml.dom;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxsource.xml.dom.nodeoperation.NameNodeLocator;
import jxsource.xml.dom.nodeoperation.PathElementLocator;
import jxsource.xml.dom.nodeoperation.TextNodeLocator;
import jxsource.xml.io.XmlOutputStream;
import jxsource.xml.parser.Parser;

import org.junit.Before;
import org.junit.Test;

public class DocumentTest {

	Document doc;
	@Before
	public void init() {
		try {
			FileInputStream in = new FileInputStream("src/test/resource/yahooHistory.html");
			Parser parser = new Parser(in);
			DocumentBuilder docBuilder = new DocumentBuilder();
			doc = docBuilder.buildeDoc(parser);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	@Test
	public void testload() {
		for(Node node: doc.getChildren()) {
			if(node.getName().equals("object")) {
				for(Node c: ((Element)node).getChildren()) {
					if(c.getName().equals("embed")) {
//						new XmlOutputStream().print(System.out, c);
//						System.out.println();
						Element ele = (Element)c;
						Map<String, String> attrs = ele.getAttributes();
						for(Map.Entry<String, String> attr: attrs.entrySet()) {
							//System.out.println("------------");
							//System.out.println(attr.getKey()+" = "+attr.getValue());
							if(attr.getKey().equals("flashvars")) {
								StringTokenizer st = new StringTokenizer(attr.getValue(),";");
								while(st.hasMoreTokens()) {
									String value = st.nextToken();
									//System.out.println("\t"+value);
									List<String> host = matchHtml(value);
									//if(host.size()>0)
									//System.out.println(host);
								}
							} else {
								List<String> host = match(attr.getValue());
								if(host.size()>0)
								//System.out.println(host);
									ele.setAttribute(attr.getKey(), "######");
//									attr.setValue("######");
							}
						}
					}
				}
			}
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			new XmlOutputStream().write(out, doc);
//			System.out.println(new String(out.toByteArray()));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

	}
	
	private List<String> match(String src) {
		String query = new String("http://(\\w+\\.)+(\\w+(:[0-9]+)?/)".getBytes());
		Pattern pattern = Pattern.compile(query);
		Matcher matcher = pattern.matcher(src);
		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			String host = matcher.group();
			list.add(host);
		}
		return list;
	}

	private List<String> matchHtml(String src) {
		String query = new String("http%3A%2F%2F(\\w+\\.)+(\\w+(%3A[0-9]+)?%2F)".getBytes());
		Pattern pattern = Pattern.compile(query);
		Matcher matcher = pattern.matcher(src);
		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			String host = matcher.group();
			list.add(host);
		}
		return list;
	}

}
