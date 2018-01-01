package jxsource.xml.dom;

import java.util.HashSet;
import java.util.Set;

/*
 * NodeNotifier is a NodeOperator with build-in function to fire NodeEvent
 * for all registered NodeListener.
 */
public abstract class ElementNotifier implements NodeOperator{

	Set<ElementListener> listeners = new HashSet<ElementListener>();
	
	public void addListener(ElementListener listener) {
		listeners.add(listener);
	}
	
	public boolean removeListener(ElementListener listener) {
		return listeners.remove(listener);
	}
	
	public void fire(Element node) {
		for(ElementListener listener: listeners) {
			listener.fire(new ElementEvent(node));
		}
	}
}
