package jxsource.xml.dom;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;

import jxsource.xml.parser.Parser;
import jxsource.xml.parser.Token;

import org.junit.Before;
import org.junit.Test;

public class PathUtilityTest {

	Parser parser;
	PathUtility util;
	@Before
	public void init() {
		try {
			FileInputStream in = new FileInputStream("src/test/resource/yahoo.html");
			parser = new Parser(in, Charset.forName("ISO-8859-1"));
			util = new PathUtility();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	@Test
	public void testLocate() {
		String path = "/html/body";
		Token t = util.locate(parser, path);
		assertTrue(t != null);
	}

	@Test
	public void testLocateNode() {
		String path = "/html/body";
		Node node = util.locateElement(parser, path);
//		System.out.println(node.getRootPath());
		assertTrue(node.getRootPath().equals(path));
	}

	@Test
	public void testLocateNode2() {
		String path = "/html/body";
		Element startNode = util.locateElement(parser, path);
		Node node = util.locateElement(startNode, "/div");
		assertTrue(node.getRootPath().equals(path+"/div"));
	}

	@Test
	public void testLocateSubTree() {
		String path = "/html/body";
		Node startNode = util.locateElement(parser, path);
		Element node = util.locateSubTree((Element)startNode, "/div");
		assertTrue(node.getRootPath().equals(path+"/div"));
	}

	@Test
	public void testfindFirstXPathByText() {
		String txt = "Currency in USD.";
		String xpath = util.findFirstXPathByText(parser, txt);
		assertTrue(xpath.equals("/html/body/div[3]/div[4]/table[2]/tr[2]/td/p[2]/Text"));
	}
	@Test
	public void testfindFirstNodeByText() {
		String txt = "Currency in USD.";
		Node node = util.findFirstNodeByText(parser, txt);
		assertTrue(node.getRootPath().equals("/html/body/div[3]/div[4]/table[2]/tr[2]/td/p[2]/Text"));
	}

	@Test
	public void testfindXPathByText() {
		String txt = "Currency in USD.";
		Vector<String> xpath = util.findXPathByText(parser, txt);
		assertTrue(xpath.size() == 1);
		assertTrue(xpath.get(0).equals("/html/body/div[3]/div[4]/table[2]/tr[2]/td/p[2]/Text"));
	}

	@Test
	public void testGetNodeByText() {
		String txt = "Currency in USD.";
		List<Node> nodes = util.findNodeByText(parser, txt);
		assertTrue(nodes.size() == 1);
		assertTrue(nodes.get(0).getRootPath().equals("/html/body/div[3]/div[4]/table[2]/tr[2]/td/p[2]/Text"));
	}
}
