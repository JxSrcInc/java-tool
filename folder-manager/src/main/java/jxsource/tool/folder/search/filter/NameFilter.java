package jxsource.tool.folder.search.filter;

import java.util.HashSet;
import java.util.Set;

import jxsource.tool.folder.search.JFile;

/**
 * String match must be only file name excluding extension
 */
public class NameFilter extends FullNameFilter {
	
	@Override
	public boolean _accept(JFile file) {
		String name = file.getName();
		int i = name.indexOf('.');
		name = name.substring(0, i);
		for(String match: matchs) {
			if(_accept(name, match)) {
				return true;
			}
		}
		return false;
	}

}
