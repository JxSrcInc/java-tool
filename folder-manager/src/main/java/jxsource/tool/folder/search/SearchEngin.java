package jxsource.tool.folder.search;

import java.util.HashSet;
import java.util.Set;

import jxsource.tool.folder.search.action.Action;
import jxsource.tool.folder.search.filter.Filter;

public abstract class SearchEngin {
	private Set<Action> actions = new HashSet<Action>();
	private Filter filter;
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	public SearchEngin addAction(Action action) {
		actions.add(action);
		return this;
	}
	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}
	protected boolean consum(JFile file) {
		if(filter == null) {
			for(Action action: actions) {
				action.proc(file);
			}
			return true;			
		} else 
		if(filter.accept(file)) {
			for(Action action: actions) {
				action.proc(file);
			}
			return true;
		} else {
			return false;
		}
	}
	
}
	