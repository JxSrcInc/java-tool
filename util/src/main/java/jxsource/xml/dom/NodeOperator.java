package jxsource.xml.dom;

public interface NodeOperator {
	
	/**
	 * 
	 * @param node - node to process
	 * @return - true if continue to process next Node. false stop at this Node
	 */
	public boolean proc(Node node);
}
