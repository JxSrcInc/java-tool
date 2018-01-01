package jxsource.xml.dom;

import jxsource.xml.parser.Token;
public class Node{
	
	public static final String defaultRoot = "<Root>";
	/*
	 * The first valid W3C order is 1 !!!!
	 */
	int InitOrder = 0; 
	protected Token me;
	Element parent;
	int level;
	int order;
	Document doc;
	/*
	 * true when all its children are loaded 
	 * or it has no child, like Doctype, Comment and Text.
	 */
	boolean complete = false;
	
	protected Node() {}
	
	Node(Token value, Element parent)
	{
		if(parent == null)
			throw new NullPointerException("Cannot create Node without parent.");
		me = value;
		this.parent = parent;
		updateOrder();
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	void setParent(Element parent) {
		this.parent = parent;
		updateOrder();
	}
	public int getLevel() {
		return level;
	}
	private String getTokenName(Token t) {
		if(t instanceof jxsource.xml.parser.Element) {
			return ((jxsource.xml.parser.Element)t).getName();
		} else {
			return t.getClass().getSimpleName();
		}
	}

	void setDocument(Document doc) {
		this.doc = doc;
	}
	public Document getDocument() {
		return doc;
	}
	/*
	 * This method is used only when building document
	 */
	void setNodeComplete(Boolean complete) {
		this.complete = complete;
	}
	/*
	 * This method is used only when building document
	 */
	public boolean isNodeComplete() {
		return complete;
	}
	public String getName() {
		return getTokenName(me);
	}
	public Token getToken() {
		return me;
	}
	/*
	 * order of the child that has the same name in parent 
	 */
	public int getOrder() {
		return order;
	}
		
	public int updateOrder() {
		this.order = InitOrder;
		if(parent != null) {
			int order = InitOrder;
			if(parent.getToken() != null)
			for(Node node: parent.getChildren()){
				if(getTokenName(node.me).equals(getTokenName(me)))
				{
					++order;
					if(node.equals(this)) {
						this.order = order;
						break;
					}
				}
			}
		}
		return this.order;
	}
	public Element getParent() {
		return parent;
	}
	public String getPath()
	{
		if(me == null)
			return "";
		String path = getName();//me);//((jxsource.xml.parser.Element)me).getName();
		if(path.charAt(0)=='/') {
			path = '\\'+path.substring(1);
		}
		if(order > 1)
			path += "["+Integer.toString(order)+"]";
		return path;
//		} else {
//			return me.getClass().getSimpleName();
//		}
	}
	public String getRootPath()
	{
		if(parent != null) {	
			// not Root
			return parent.getRootPath()+"/"+getPath();
		}
		return "";
	}
	@Override
	public String toString() {
		return "Node [ me=" + me + ", order=" + order
				 + ", xpath=" + getRootPath()
				 + ", complete=" + this.isNodeComplete()
				 + ", parent=" + (parent==null?"null":parent.getName())
				 + "]";
	}
	
	
}
