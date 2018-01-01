package jxsource.xml.dom;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import jxsource.xml.parser.Token;
import jxsource.xml.parser.TokenSrc;

/**
 * DESIGN CHANGE -- 2011/04/22
 * Parser send in single token "</element>". 
 * DocumentBuilder adds it as the last child in <element> Node 
 *
 */
public class XPathChooser {
	
	public static final int NotFound = -1;
	public static final int OpenNode = 0;
	public static final int CloseNode = 1;
	
	TokenSrc parser;
	Document doc;
	PathBuilder pathBuilder;
	Node foundNode;
	/*
	 * All Nodes in the document in order 
	 */
	List<Node> nodes = new ArrayList<Node>();
	
	TreeProcessor levelUpdater;
	TreeProcessor pathUpdater;
	int status;

	public XPathChooser(TokenSrc parser) {
		this.parser = parser;
		doc = new Document();
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
				node.updateOrder();
				return true;
			}
		});
		pathBuilder = new PathBuilder();
	}
	
	public Node getFoundNode() {
		return foundNode;
	}
	
	public int getStatus() {
		return status;
	}
	public int searchNode(String searchPath) {
		pathBuilder.setSearchPath(searchPath);
//		System.out.println("sPath = "+pathBuilder.getSearchPathValue());
//		nodes.clear();
		try {
			foundNode = buildNode(doc, searchPath);
//			System.out.println(foundNode+","+nodes.get(nodes.size()-1));
			if(foundNode.isNodeComplete()) {
				status = CloseNode;
			} else {
				status = OpenNode;
			}
		} catch(Exception e) {
			status = NotFound;
		}
		return status;
	}
	
	/*
	 * Create a Node from Parameter Token 
	 * and add it to List<Node> nodes
	 */
	private Node addToNodeList(Element parent, Token t) {
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

	private Node buildNode(Element parent, String searchPath) throws EOFException{
		Token token = null;
		boolean found = false;
		while(!found) {
			// 1. Read Tag
			token = parser.getNextToken();
//			System.out.println("* "+token);
			// A. End stream>
			if(token == null) {
				// 2:Yes - end
				throw new EOFException("Error: End of InputStream.");
			}
			if(token instanceof jxsource.xml.parser.Element) {
				jxsource.xml.parser.Element tEle = (jxsource.xml.parser.Element) token;
				// 3. Add node to List<Node> nodes 
				// addToNodeList returns the last node in nodes
				// and saved in fouindNode variable
				//
				foundNode = addToNodeList(parent, token);					
				// B. End Tag?
				if(tEle.isElementEndTag()) {
					// 5. rebuild List<Node> nodes
					rebuildNodeList(tEle);
//					System.out.println("# "+tEle);
//				} else {
					// 3. Add node to List<Node> nodes 
//					foundNode = addToNodeList(parent, token);					
				}
				// 4. build path
				String pathValue = pathBuilder.buildPath(nodes);
//				System.out.println(pathValue);
				// C. Does path equals searchPath?
				if(searchPath.equals(pathValue)) {
					// fully match / equals
					// TODO: quick fix for the TODO question below.
					if(foundNode.getName().substring(1).equals(nodes.get(nodes.size()-1).getName())) {
						foundNode = nodes.get(nodes.size()-1);
					}
					// 6. Find Node
					found = true;
				} else 
				if(searchPath.indexOf(pathValue) == 0) {
					// parrial match / equals
					// D. Is the last Element close?
					Element lastEle = getLastElement();
					if(lastEle.complete) {
						this.rebuildNodeList(lastEle);
						// 7. find close match
						foundNode = findCloseNode(lastEle, searchPath, pathValue);
						// E. Find close node
						if(foundNode != null) {
							// 6. Find node
							found = true;
						} else {
							// 8. Not find
							// tags: <a><b>...</b>
							// searchpath: a/b/c
							// possible path after this point:
							// 	1. a/? where ? is not b
							//  2. a/b[?] where ? is a number > 1
							found = false;
						}
						// else - Read next Tag
					} 
					// else - Read next Tag
				} else {
					// not match - invalid path
					// Read next Tag.
					// Because it is possible some parent node will close later 
				}
			} else {
				// Text and other not-Element Nodes, add as children
				addToNodeList(parent, token);
			}
		}
//		System.out.println("path: "+foundNode.getRootPath()+","+pathBuilder.getPathValue());
		return foundNode;
//		pathUpdater.run(doc);
	}
	
	private Element getLastElement() {
		for(int i=nodes.size()-1; i>=0; i--) {
			Node node = nodes.get(i);
			if(node instanceof Element) {
				return (Element) node;
			}
		}
		return null;
	}
	/*
	 * Open Node is handled as a sibling like in DocumentBuilder.build()
	 */
	private Node findCloseNode(Element ele, String searchPath, String path) {
//		if(!(node instanceof Element)) {
//			throw new RuntimeException(node+" is not Element.");
//		} 
//		System.out.println("? "+path);
		Node ret = null;
		if(path.equals(searchPath)) {
//			System.out.println("* "+path+","+searchPath);
			ret = ele;
		} else {
		if(ele.isNodeComplete()) {
			for(Node child: ele.getChildren()) {
				if(child instanceof Element) {
					child.updateOrder();
					ret = findCloseNode((Element)child, searchPath, path+'/'+child.getPath());
					if(ret != null) {
						break;
					}
				}
			}
		} // else - skip open node
		}
//		System.out.println("**** "+searchPath+","+ele+","+ret);
		return ret;
	}
	
	private void rebuildNodeList(Element ele) {
		rebuildNodeList((jxsource.xml.parser.Element) ele.getToken());
	}

	/*
	 * Implementation of step 5. 
	 */
	private void rebuildNodeList(jxsource.xml.parser.Element tEle) {
		/*
		 * 1. find the closet node in nodes 
		 * which has the same name as token
		 * 
		 */ 
//		System.out.println("* "+tEle.getName());
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
						// create the end Element for dirEle
//						addToNodeList(parent, tEle);
						addChildren(i + 1, dirEle);
//						System.out.println("@ "+nodes.get(nodes.size()-1));
						pathBuilder.buildPath(nodes);
					} // else - skip becuase there is nothing between <tag> and </tag>
					break;
				}
			}
		}
		
	}
	
	private void addChildren(int startIndex, Element dirEle) {
		int endIndex = nodes.size();
//		System.out.println(">> "+nodes.size()+","+startIndex+","+dirEle.getName());
		for(int k=startIndex; k<endIndex; k++) {
			Node child = nodes.remove(startIndex);
			// reset parent
			child.setParent(dirEle);
			// add child
			dirEle.addChild(child);
		}
		levelUpdater.run(dirEle);
		pathUpdater.run(dirEle);
//		for(Node child: dirEle.children)
//		System.out.println(child);
		
	}
	
}
