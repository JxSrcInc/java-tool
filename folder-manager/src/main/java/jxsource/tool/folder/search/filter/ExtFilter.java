package jxsource.tool.folder.search.filter;

import jxsource.tool.folder.search.JFile;

public class ExtFilter extends Filter{
	String[] exts;
	public ExtFilter(String[] exts) {
		for(int i=0; i<exts.length; i++) {
			exts[i] = exts[i].trim();
		}
		this.exts = exts;
	}
	public ExtFilter(String exts) {
		exts = exts.replaceAll(" ", "");
		this.exts = exts.split(",");
	}
	
	public int _accept(JFile f) {
		if(f.isDirectory()) {
			return Filter.PASS;
		}
		String name = f.getName();
		int index = name.lastIndexOf('.');
		if(index > 0)  {
			String ext = name.substring(index+1);
			for(String match: exts) {
				if(ext.equals(match)) {
					return Filter.ACCEPT;
				}
			}
		}
		return Filter.REJECT;
	}

}
