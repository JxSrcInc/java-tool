package jxsource.tool.folder.search;

import java.io.File;

import jxsource.tool.folder.search.action.FilePrintAction;
import jxsource.tool.folder.search.filter.ExtFilter;

public class SysSearchEngin extends SearchEngin {
	
	public void search(File file) {
		if(consum(new SysFile(file))) {
			if(file.isDirectory()) {
			for(File child: file.listFiles()) {
				search(child);
			}
			}
		}
	}
	
}
