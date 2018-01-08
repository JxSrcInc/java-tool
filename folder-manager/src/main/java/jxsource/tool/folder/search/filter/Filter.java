package jxsource.tool.folder.search.filter;

import jxsource.tool.folder.search.JFile;

public abstract class Filter{
	public static final int ACCEPT = 1;
	public static final int PASS = 0;
	public static final int REJECT = -1;
	Filter next;
	public void setNext(Filter filter) {
		next = filter;
	}
	public abstract int _accept(JFile file);
	
	public int accept(JFile file) {
		switch(_accept(file)) {
			case ACCEPT:
				if(next != null) {
					return next.accept(file);
				} else {
					return ACCEPT;
				}
			case PASS:
				return PASS;
			default:
				return REJECT;
		}
	}
}
