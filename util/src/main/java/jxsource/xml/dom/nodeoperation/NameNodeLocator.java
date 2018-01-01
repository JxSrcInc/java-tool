package jxsource.xml.dom.nodeoperation;

import java.util.ArrayList;
import java.util.List;

import jxsource.xml.dom.Node;
import jxsource.xml.dom.NodeEvent;
import jxsource.xml.dom.NodeListener;
import jxsource.xml.dom.NodeNotifier;
import jxsource.xml.dom.TreeProcessor;

public class NameNodeLocator extends TreeProcessor implements NodeListener {
	
	List<Node> foundNodes = new ArrayList<Node>();
	public void fire(NodeEvent event) {
		foundNodes.add(event.getValue());
	}
	
	/*
	 * if all, find all nodes with given name
	 * otherwise, find the first node with given name only
	 */
	public List<Node> findNodes(final String name, Node startNode, final boolean...all) {
		NodeNotifier notifier = null;
		addProcessor(notifier = new NodeNotifier() {
			public boolean proc(Node node) {
				if(node.getName().equals(name)) {
					fire(node);
					if(all.length > 0) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			}
		});
		notifier.addListener(this);
		run(startNode);
		return foundNodes;	
	}

	public Node findFirstNode(final String name, Node startNode) {
		findNodes(name, startNode);
		if(foundNodes.size() > 0) {
			return foundNodes.get(0);
		} else {
			return null;
		}
	}
}
