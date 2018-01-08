package jxsource.tool.folder.search.match;

import jxsource.tool.folder.search.JFile;

public abstract class Match {
	protected boolean ignoreCase;
	protected String match;
	
	/**
	 * must create in MatchFactory
	 * and call setMatch() to set match value
	 */
	Match() {}
	void init(String match, boolean ignoreCase) {
		this.match = match;		
		this.ignoreCase = ignoreCase;
	}
	
	public boolean match(String nodeName) {
		if(ignoreCase) {
			return match(nodeName.toLowerCase(), match.toLowerCase());
		} else {
			return match(nodeName, match);
		}
	}
	
	/**
	 * 
	 * @param nodeName - node is a segment of JFile path, which is also a JFile
	 * 		nodeName is the value of getName() of the JFile
	 * @return
	 */
	public abstract boolean match(String nodeName, String match);
}
