package jxsource.xml.dom;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.util.List;

import jxsource.xml.dom.nodeoperation.NameNodeLocator;
import jxsource.xml.dom.nodeoperation.PathElementLocator;
import jxsource.xml.dom.nodeoperation.TextNodeLocator;
import jxsource.xml.parser.Parser;

import org.junit.Before;
import org.junit.Test;

public class TreeProcessorTest {

	Document doc;
	@Before
	public void init() {
		try {
			FileInputStream in = new FileInputStream("src/test/resource/yahoo.html");
			Parser parser = new Parser(in);
			DocumentBuilder docBuilder = new DocumentBuilder();
			doc = docBuilder.buildeDoc(parser);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	@Test
	public void testPathNodeLocator() {
		PathElementLocator treeProcessor = new PathElementLocator();
		Node node = treeProcessor.findElement("/html", doc.getDocument());
		assertTrue(node.getName().equals("html"));
	}

	@Test
	public void testNameNodeLocatorAll() {
		NameNodeLocator treeProcessor = new NameNodeLocator();
		List<Node> nodes = treeProcessor.findNodes("div", doc.getDocument(), true);
//		System.out.println(nodes.size());
		assertTrue(nodes.size() == 59);
	}
	@Test
	public void testNameNodeLocator() {
		PathElementLocator pathNodeLocator = new PathElementLocator();
		String path = "/html/body";
		Node node = pathNodeLocator.findElement(path, doc.getDocument());
		NameNodeLocator treeProcessor = new NameNodeLocator();
		List<Node> nodes = treeProcessor.findNodes("div", node);
		assertTrue(nodes.size() == 1);
//		System.out.println(nodes.get(0).getPath());
		assertTrue(nodes.get(0).getRootPath().equals(path+"/div"));
		assertTrue(nodes.get(0).getPath().equals("div"));
	}

	@Test
	public void testTextNodeLocator() {
		String s = "Currency in USD.";
		TextNodeLocator treeProcessor = new TextNodeLocator();
		List<Node> nodes = treeProcessor.findNodes(s, doc.getDocument(), true);
//		System.out.println(nodes.get(0).toString());
		assertTrue(nodes.size() == 1);
		assertTrue(nodes.get(0).toString().trim().equals(s));
		}
}
