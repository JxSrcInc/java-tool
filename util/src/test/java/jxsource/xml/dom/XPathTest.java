package jxsource.xml.dom;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import jxsource.xml.io.XmlOutputStream;
import jxsource.xml.parser.Parser;

import org.junit.Test;

public class XPathTest {
	
	static final String data = "<html><body>"
		+"<table><tr></table>"
		+"<table><test><div></div></test></table>"
		+"<table><div><test></test></div></table>"
		+"<table><t><div><test>ads</test></div><div>table[4]/div[2]</div></table>"
		+"</body></html>";
	private XPathChooser test(String searchPath, String... src) {
		String xml = data;
		if(src.length > 0) {
			xml = src[0];
		}
		try {
			InputStream in = new ByteArrayInputStream(xml.getBytes());
			Parser parser = new Parser(in,Charset.forName("ISO-8859-1"));
			XPathChooser builder = new XPathChooser(parser);
			builder.searchNode(searchPath);
			return builder;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	private String print(Node node) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			new XmlOutputStream().write(out, node);
			out.close();
			return new String(out.toByteArray());
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}

	}

	@Test
	public void testXPath1() {
		String path = "/html/body/table[4]/div[2]";
		XPathChooser builder = test(path);
		int status = builder.getStatus();
		assertTrue(status == XPathChooser.CloseNode);
		Node node = builder.getFoundNode();
		assertTrue(((Element)node).getText().trim().equals("table[4]/div[2]"));
	}

	@Test
	public void testXPath2() {
		String path = "/html/body/table[3]/div";
		XPathChooser builder = test(path);
		int status = builder.getStatus();
		assertTrue(status == XPathChooser.OpenNode);
		Node node = builder.getFoundNode();
		assertTrue(node.getRootPath().equals("/div"));
	}

	@Test
	public void testXPath3() {
		String path = "/html/body/table[2]/div";
		XPathChooser builder = test(path);
		int status = builder.getStatus();
		assertTrue(status == XPathChooser.NotFound);
	}
	@Test
	public void testXPath4() {
		String path = "/html/body/table[4]/div/test";
		XPathChooser builder = test(path);
		int status = builder.getStatus();
		assertTrue(status == XPathChooser.CloseNode);
		Node node = builder.getFoundNode();
		assertTrue(node.getRootPath().equals("/table/div/test"));
	}
	@Test
	public void testXPath5() {
		final String src = "<html><body>"
			+"<table><tr></table>"
			+"<table><test><div></div></test></table>"
			+"<table><div><test></test></div></table>"
			+"<table><t><div><test>Test Message</test></div><div></div></table>"
			+"</body></html>";
		String path = "/html/body/table[4]/div/test";
		XPathChooser builder = test(path, src);
		int status = builder.getStatus();
		assertTrue(status == XPathChooser.CloseNode);
		Node node = builder.getFoundNode();
		assertTrue(print(node).equals("<test>Test Message</test>"));
	}

	@Test
	public void testHtml() {
		final String src = "<html><head><meta title=\"title\"/></head>"
			+"<body>"
			+"<table><tr></table>"
			+"<table><test><div></div></test></table>"
			+"<table><div><test></test></div></table>"
			+"<table><t><div><test><![CDATA[Test Message]]></test></div><div></div></table>"
			+"</body></html>";
		String path = "/html/body/table[4]/div/test";
		XPathChooser builder = test(path, src);
		int status = builder.getStatus();
		assertTrue(status == XPathChooser.CloseNode);
		Node node = builder.getFoundNode();
		assertTrue(print(node).equals("<test><![CDATA[Test Message]]></test>"));
	}

	@Test
	public void testSearchCompletedNode() {
		final String src = "<html><body>"
			+"<table><tr></table>"
			+"<table><test><div></div></test></table>"
			+"<table><div><test></test>Test Message</div></table>"
			+"<table><t><div><test></test></div><div></div></table>"
			+"</body></html>";
		String path = "/html/body/table[3]/div";
		try {
			InputStream in = new ByteArrayInputStream(src.getBytes());
			Parser parser = new Parser(in);
			XPathChooser builder = new XPathChooser(parser);
			builder.searchNode(path);
			int status = builder.getStatus();
			assertTrue(status == XPathChooser.OpenNode);
			builder.searchNode(path);
			status = builder.getStatus();
			assertTrue(status == XPathChooser.CloseNode);
			Node node = builder.getFoundNode();
//			System.out.println(print(node));
			assertTrue(print(node).equals("<div><test></test>Test Message</div>"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
}
