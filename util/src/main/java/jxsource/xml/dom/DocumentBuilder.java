package jxsource.xml.dom;

import java.util.ArrayList;
import java.util.List;

import jxsource.xml.parser.Parser;
import jxsource.xml.parser.Token;
import jxsource.xml.parser.TokenSrc;

/**
 * DESIGN CHANGE -- 2011/04/22
 * Parser send in single token "</element>". 
 * DocumentBuilder adds it as the last child in <element> Node 
 *
 */
public class DocumentBuilder {
	
	TokenSrc parser;
	Document doc;
	
	/*
	 * All Nodes in the document in order 
	 */
	List<Node> nodes = new ArrayList<Node>();
	
	TreeProcessor levelUpdater;
	TreeProcessor pathUpdater;

	public DocumentBuilder() {
		levelUpdater = new TreeProcessor();
		levelUpdater.addProcessor(new NodeOperator() {
			public boolean proc(Node node) {
				node.setLevel(node.getLevel()+1);
				return true;
			}
		});
		pathUpdater = new TreeProcessor();
		pathUpdater.addProcessor(new NodeOperator() {
			public boolean proc(Node node) {
//				node.updateXpath();
				node.updateOrder();
				return true;
			}
		});
	}
	
	
	
	public Document buildeDoc(TokenSrc parser) {
		this.parser = parser;
		doc = new Document();
		nodes.clear();
		buildNode(doc);
		return doc; 
		
	}
	
	/*
	 * Create a Node from Parameter Token 
	 * and add it to List<Node> nodes
	 */
	private Node createNode(Element parent, Token t) {
		Node node = null;
		if(t instanceof jxsource.xml.parser.Element) {
			node = new Element(t, parent);
		} else if(t instanceof jxsource.xml.parser.Text) {
			node = new Text(t, parent);
			// Text automatically sets nodeComplete to true
		} else {
			// like Comment or DocType of jxsource.xml.parser.Tag
			node = new Node(t, parent);
			node.setNodeComplete(true);
		}
		nodes.add(node);
		return node;
	}

	private void buildNode(Element parent) {
		while(true) {
			Token token = parser.getNextToken();
			if(token == null) {
				// end 
				break;
			}
			if(token instanceof jxsource.xml.parser.Element) {
				jxsource.xml.parser.Element tEle = (jxsource.xml.parser.Element) token;
				if(tEle.isElementEndTag()) {
					/*
					 * 1. find the closet node in nodes 
					 * which has the same name as token
					 * 
					 */ 
//					System.out.println("* "+tEle.getName());
					for(int i=nodes.size()-1; i>=0; i--) {
						Node dirNode = nodes.get(i);
						if(dirNode instanceof Element) {
							Element dirEle = (Element) dirNode;
							if(dirEle.getName().equals(tEle.getName().substring(1)) &&
								!dirEle.isNodeComplete()) {
								/* 
								 * 2. add all item(node)s in List<Node>(nodes) 
								 * which are under the node found in above step 
								 */
								/*
								 * Note. if there is no element between <tag> and </tag>
								 * then <tag> is the last element in nodes.
								 * So the condition is i < nodes.size(),
								 * but startIndex is i+1 because </tag> is added to nodes
								 *
								 */
								if(i < nodes.size()) {
									// create end tag Node in nodes list
									// so addChildren will make it as child of start tag Node
									dirEle.setNodeComplete(true);
									createNode(parent, token);
									addChildren(i + 1, dirEle);
								} // else - skip becuase there is nothing between <tag> and </tag>
								break;
							}
						}
					}
				} else {
					// Element as children
					createNode(parent, token);
				}
			} else {
				// Text and other Nodes as children
				createNode(parent, token);
			}
		}
		addChildren(0, doc);
		pathUpdater.run(doc);
	}
	
	private void addChildren(int startIndex, Element dirEle) {
		int endIndex = nodes.size();
//		System.out.println(">> "+nodes.size()+","+startIndex+","+dirEle.getName());
		for(int k=startIndex; k<endIndex; k++) {
			Node child = nodes.remove(startIndex);
			// reset parent
			child.setParent(dirEle);
			levelUpdater.run(child);
			// add child
			dirEle.addChild(child);
		}	
	}
	
	public static void main(String[] args) {
		try {
			java.io.FileInputStream in = new java.io.FileInputStream("C:\\dev\\eclipse\\httpproxy\\http-xpath\\t.html");
			Document doc = new DocumentBuilder().buildeDoc(new Parser(in));
			System.out.println(new jxsource.xml.io.XmlOutputStream().getString(doc));

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
