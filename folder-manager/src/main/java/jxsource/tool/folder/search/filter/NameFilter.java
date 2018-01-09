package jxsource.tool.folder.search.filter;

import java.util.HashSet;
import java.util.Set;

import jxsource.tool.folder.search.JFile;

/**
 * String match must be only file name excluding extension
 */
public class NameFilter extends FullNameFilter {
	
	@Override
	public int _accept(JFile file) {
		if(file.isDirectory()) {
			return Filter.PASS;
		}
		String name = file.getName();
		int i = name.indexOf('.');
		if(i > 0) {
			name = name.substring(0, i);
		}
		for(String match: matchs) {
			if(_accept(name, match)) {
				return Filter.ACCEPT;
			}
		}
		return Filter.REJECT;
	}

}
