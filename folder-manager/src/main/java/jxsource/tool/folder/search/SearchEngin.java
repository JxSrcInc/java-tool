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

	/**
	 * check filter to determine if file should be
	 * 	1. accept: apply all actions. the JFile can be directory or file
	 * 		Action may work on only one type or both.
	 * 		it is Action's responsibility to determine whether the JFile is directory or file.
	 *  2. pass: the file is on valid path
	 *  3. reject: the file is not on valid path
	 * @param file
	 * @return true to allow search engine to process its children
	 * 		false inform search engine to stop process its children
	 */
	protected boolean consum(JFile file) {
		if (filter == null) {
			for (Action action : actions) {
				action.proc(file);
			}
			return true;
		} else {
			switch (filter.accept(file)) {
			case Filter.ACCEPT:
				for (Action action : actions) {
					action.proc(file);
				}
			case Filter.PASS:
				return true;
			default:
				return false;
			}
		}
	}

}
