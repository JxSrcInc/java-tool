package jxsource.xml.dom;

import java.util.Map;
import java.util.Vector;
import jxsource.xml.parser.Tag;
import jxsource.xml.parser.Token;

public class Element extends Node{
	
	
	Vector<Node> children = new Vector<Node>();
	boolean hasAttributes = false;
	protected jxsource.xml.parser.Element ele;
	
	protected Element() {}
	
	public Element(Token token, Element parent)
	{
		super(token, parent);
		if(!(token instanceof jxsource.xml.parser.Element)) {
			throw new RuntimeException("Not jxsource.xml.parser.Element - "+token.getClass().getName());
		}
		ele = (jxsource.xml.parser.Element)token;
	}
	
	public String getAttribute(String name) {
		return ele.getAttribute(name);
	}
	
	public Map<String,String> getAttributes() {
		if(ele == null) {
			System.out.println(me);
		}
		return ele.getAttributes();
	}
	
	public void addChild(Node node)
	{
		children.add(node);
		node.updateOrder();
//		node.xpath = xpath+"/"+node.getPath();
	}
	public Node getChild(int i)
	{
		return children.get(i);
	}
	public Vector<Node> getChildren()
	{
		return children;
	}
	
	public Vector<Node> getNodesByName(String name) {
		Vector<Node> ret = new Vector<Node>();
		for(Node node: getChildren()) {
			if(node.getName().equals(name)) {
				ret.add(node);
			} else
			if(node instanceof Element) {
				for(Node child: ((Element)node).getNodesByName(name))
					ret.add(child);
			} 
		}
		return ret;
	}

	public String getText() {
		String ret = "";
		for(Node node: getChildren()) {
			if(node instanceof Element) {
				ret += ((Element)node).getText()+" ";
			} else 
			if (node instanceof Text) {
				ret += ((Text)node).getText()+" ";
			}
		}
		return ret;
	}
	
	public boolean isSingleElement()
	{
		return ((Tag)me).isSingle();
	}

	public boolean isEndTagElement()
	{
		return ((jxsource.xml.parser.Element)me).isElementEndTag();
	}

	public void setAttribute(String name, String value) {
		ele.setAttribute(name, value);
	}
	
	public void deleteAttribute(String name) {
		ele.deleteAttribute(name);
	}
	
	public String getValue() {
		return me.toString();
	}
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<a href=\"/portfolios/\">");
		jxsource.xml.parser.Element pe = new jxsource.xml.parser.Element(sb);
		Document doc = new Document();
		Element e = new Element(pe, doc);
		System.out.println(e.getName().equals("a"));
		System.out.println(e.isNodeComplete());
		System.out.println(e.isSingleElement());
		System.out.println(e.getAttribute("href"));
		
	}

}
