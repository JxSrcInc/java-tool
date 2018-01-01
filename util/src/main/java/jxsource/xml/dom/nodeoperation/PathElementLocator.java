package jxsource.xml.dom.nodeoperation;

import jxsource.xml.dom.Element;
import jxsource.xml.dom.ElementEvent;
import jxsource.xml.dom.ElementListener;
import jxsource.xml.dom.ElementNotifier;
import jxsource.xml.dom.Node;
import jxsource.xml.dom.TreeProcessor;

public class PathElementLocator extends TreeProcessor implements ElementListener {
	
	Element foundNode;
	public void fire(ElementEvent event) { 
		foundNode = event.getValue();
	}
	
	/**
	 * 
	 * @param searchPath -- relative path to startNode
	 * @param startNode
	 * @return
	 */
	public Element findElement(final String searchPath, Element startElement) {
		final String path = startElement.getRootPath()+searchPath;
		ElementNotifier notifier = new ElementNotifier() {
			public boolean proc(Node node) {
				if(node.getRootPath().equals(path) && node instanceof Element) {
					fire((Element)node);
					return false;
				} else {
					return true;
				}
			}
		};
		this.addProcessor(notifier);
		notifier.addListener(this);
		run(startElement);
		return foundNode;	
	}

}
