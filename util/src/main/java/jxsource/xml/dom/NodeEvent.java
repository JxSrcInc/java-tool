package jxsource.xml.dom;

public class NodeEvent {

	private final Node node;
	
	public NodeEvent(Node node) {
		this.node = node;
	}
	
	public Node getValue() {
		return node;
	}
}
