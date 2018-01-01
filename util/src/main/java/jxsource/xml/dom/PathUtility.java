package jxsource.xml.dom;

import java.util.List;
import java.util.Vector;

import jxsource.xml.dom.nodeoperation.PathElementLocator;
import jxsource.xml.dom.nodeoperation.TextNodeLocator;
import jxsource.xml.parser.Token;
import jxsource.xml.parser.TokenSrc;
/**
 * This class is reusable. 
 * 	- use setXPath to setup XPath.
 *  - use search(XmlInputFilter) to find a Node specified by XPath in the given input stream.
 *  - use findXPath(XmlInputFilter) to find the full XPath that contains the value of xpath property 
 *  	in the given input stream. 
 */
public class PathUtility {


	/**
	 * Find the token by relative xpath defined in XPath 
	 * 		starting from the returned token of parser.getNextToken() method.
	 * @param parser
	 * @return - Token found in parser
	 * @throws XPathException - reach the end of parser input stream
	 */
	public Token locate(TokenSrc parser, String xpath) 
	{
		Node node = locateElement(parser, xpath);
		if(node != null) {
			return node.getToken();
		} else {
			return null;
		}
	}

	/**
	 * returned node does not include its children
	 * parser stops at the next token.
	 * 
	 * @param parser
	 * @param xpath
	 * @return
	 */
	public Element locateElement(TokenSrc parser, String xpath) 
	{
		DocumentBuilder docBuilder = new DocumentBuilder();
		Document doc = docBuilder.buildeDoc(parser);
		return new PathElementLocator().findElement(xpath, doc);
	}

	/*
	 * This method is more general than locateSubTree() method
	 * -- Same value but different return type
	 */
	public Element locateElement(Element startElement, String xpath) 
	{
		return new PathElementLocator().findElement(xpath, startElement);
	}

	/**
	 * This method recursively finds an element specified by xpath start from treeNode
	 * The xpath must start from treeNode but excludes the name of the treeNode.
	 * 
	 * @param treeNode
	 * @param xpath
	 * @return -- Element specified by xpath or null if no element found
	 */
	public Element locateSubTree(Element treeNode, String xpath) 
	{
		Node node = new PathElementLocator().findElement(xpath, treeNode);
		if(node != null && node instanceof Element) {
			return (Element)node;
		} else {
			return null;
		}
	}
 
	public String findFirstXPathByText(TokenSrc parser, String text) 
	{
		Node node = findFirstNodeByText(parser, text);
		if(node == null) {
			return null;
		} else {
			return node.getRootPath();
		}
	}
	public Node findFirstNodeByText(TokenSrc parser, String text) 
	{
		DocumentBuilder docBuilder = new DocumentBuilder();
		Document doc = docBuilder.buildeDoc(parser);
		return new TextNodeLocator().findFirstNode(text, doc);
	}
	public Vector<String> findXPathByText(TokenSrc parser, String text) 
	{
		Vector<String> paths = new Vector<String>();
		for(Node node: findNodeByText(parser, text)) {
			paths.add(node.getRootPath());
		}
		return paths;
	}
	public List<Node> findNodeByText(TokenSrc parser, String text) 
	{
		DocumentBuilder docBuilder = new DocumentBuilder();
		Document doc = docBuilder.buildeDoc(parser);
		return new TextNodeLocator().findNodes(text, doc);
	}
}
