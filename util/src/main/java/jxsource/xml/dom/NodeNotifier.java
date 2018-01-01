package jxsource.xml.dom;

import java.util.HashSet;
import java.util.Set;

/*
 * NodeNotifier is a NodeOperator with build-in function to fire NodeEvent
 * for all registered NodeListener.
 */
public abstract class NodeNotifier implements NodeOperator{

	Set<NodeListener> listeners = new HashSet<NodeListener>();
	
	public void addListener(NodeListener listener) {
		listeners.add(listener);
	}
	
	public boolean removeListener(NodeListener listener) {
		return listeners.remove(listener);
	}
	
	public void fire(Node node) {
		for(NodeListener listener: listeners) {
			listener.fire(new NodeEvent(node));
		}
	}
	public abstract boolean proc(Node node);
}
