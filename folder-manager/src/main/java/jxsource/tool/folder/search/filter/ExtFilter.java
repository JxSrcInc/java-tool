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
	
	public boolean _accept(JFile f) {
		if(f.isDirectory()) {
			return true;
		}
		String name = f.getName();
		int index = name.lastIndexOf('.');
		if(index > 0)  {
			String ext = name.substring(index+1);
			for(String match: exts) {
				if(ext.equals(match)) {
					return true;
				}
			}
		}
		return false;
	}

}
