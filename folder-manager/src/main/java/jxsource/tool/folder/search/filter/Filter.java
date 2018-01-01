package jxsource.tool.folder.search.filter;

import jxsource.tool.folder.search.JFile;

public abstract class Filter{
	Filter next;
	public void setNext(Filter filter) {
		next = filter;
	}
	public abstract boolean _accept(JFile file);
	public boolean accept(JFile file) {
		if(_accept(file)) {
			if(next != null) {
				return next.accept(file);
			} else {
				return true;
			}
		} else {
			return false;
		}
		
	}
}
