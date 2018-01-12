package jxsource.tool.folder.search.filter;

import jxsource.tool.folder.search.JFile;

/**
 * The accept result can be reversed by change the value of exclude
 * default exclude is false, which implies normal acceptance
 * set exclude to true reverse the filter result
 *
 */
public abstract class Filter {
	public static final int ACCEPT = 1;
	public static final int PASS = 0;
	public static final int REJECT = -1;
	private Filter next;
	private boolean exclude;

	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}

	public void setNext(Filter filter) {
		next = filter;
	}

	public abstract int _accept(JFile file);

	public int accept(JFile file) {
		if (!exclude) {
			switch (_accept(file)) {
			case ACCEPT:
				if (next != null) {
					return next.accept(file);
				} else {
					return ACCEPT;
				}
			case PASS:
				return PASS;
			default:
				return REJECT;
			}
		} else {
			// Not support next Filter feather because unclear logic
			// TODO: not complete. need more work
			// should work for simple Filter
			switch (_accept(file)) {
			case ACCEPT:
				return REJECT;
			case PASS:
				return PASS;
			default:
				return ACCEPT;
			}

		}
	}
}
