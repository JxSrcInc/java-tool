package jxsource.xml.dom;

import java.util.HashSet;
import java.util.Set;

/*
 * Tree processor goes through all nodes in the tree and
 * applies all registered Node operators on each node.
 * 
 * The process stops when one NodeOperator.proc(Node) method returns false 
 * on any node. 
 */
public class TreeProcessor {

	Set<NodeOperator> processors = new HashSet<NodeOperator>();
	
	public void addProcessor(NodeOperator processor) {
		processors.add(processor);
	}

	/*
	 * Stop if one NodeOperator fails on any Node
	 */
	public boolean run(Node node) {
		for(NodeOperator nm: processors) {
			// if one processor requires stop, then stop
			if(!nm.proc(node)) {
				return false;
			}
		}
		if(node instanceof Element) {
			Element ele = (Element) node;
			for(Node child: ele.getChildren()) {
				// backup child status
				if(!run(child))
					return false;
			}
		}
		return true;
	}
	
	/*
	 * Goes through all Nodes no matter if NodeOperator does not throw Exception
	 */
	public void runAll(Node node) {
		for(NodeOperator nm: processors) {
			// if one processor requires stop, then stop
			nm.proc(node);
		}
		if(node instanceof Element) {
			Element ele = (Element) node;
			for(Node child: ele.getChildren()) {
				// backup child status
				runAll(child);
			}
		}
	}
	

}
