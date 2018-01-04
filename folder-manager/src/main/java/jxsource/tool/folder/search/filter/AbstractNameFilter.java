package jxsource.tool.folder.search.filter;

import java.util.HashSet;
import java.util.Set;

import jxsource.tool.folder.search.JFile;

public abstract class AbstractNameFilter extends Filter {

	private boolean ignoreCase = true;
	private boolean like = false;
	
	public boolean isIgnoreCase() {
		return ignoreCase;
	}
	public AbstractNameFilter setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
		return this;
	}
	public boolean isLike() {
		return like;
	}
	public AbstractNameFilter setLike(boolean like) {
		this.like = like;
		return this;
	}
	protected boolean _accept(String src, String match) {
		if(ignoreCase) {
			src = src.toLowerCase();
			match = match.toLowerCase();
		}
		if(like) {
			return src.contains(match);
		} else {
			return src.equals(match);
		}
	}

}
