package jxsource.xml.dom.nodeoperation;

import java.util.ArrayList;
import java.util.List;

import jxsource.xml.dom.Node;
import jxsource.xml.dom.NodeEvent;
import jxsource.xml.dom.NodeListener;
import jxsource.xml.dom.NodeNotifier;
import jxsource.xml.dom.Text;
import jxsource.xml.dom.TreeProcessor;

public class TextNodeLocator extends TreeProcessor implements NodeListener {
	
	List<Node> foundNodes = new ArrayList<Node>();
	public void fire(NodeEvent event) {
		foundNodes.add(event.getValue());
	}
	
	public List<Node> findNodes(final String text, Node startNode, final boolean...all) {
		NodeNotifier notifier = null;
		addProcessor(notifier = new NodeNotifier() {
			public boolean proc(Node node) {
				if(node instanceof Text && node.toString().trim().equals(text)) {
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

	public Node findFirstNode(final String text, Node startNode) {
		findNodes(text, startNode);
		if(foundNodes.size() > 0) {
			return foundNodes.get(0);
		} else {
			return null;
		}
	}
}
