package jxsource.tool.folder.search.template;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.zip.ZipInputStream;

import jxsource.tool.folder.search.JFile;
import jxsource.tool.folder.search.SysSearchEngin;
import jxsource.tool.folder.search.ZipSearchEngin;
import jxsource.tool.folder.search.action.Action;
import jxsource.tool.folder.search.action.CollectionAction;
import jxsource.tool.folder.search.filter.ExtFilter;
import jxsource.tool.folder.search.filter.Filter;

public class ZipSearchPrinter {
	
	class ZipAction implements Action {
		CollectionAction ca = new CollectionAction();
		public void proc(JFile f) {
			try {
				ZipInputStream in =new ZipInputStream(new FileInputStream(f.getPath()));
				ZipSearchEngin engin = new ZipSearchEngin();
				engin.addAction(ca);
//				engin.setFilter(filter);
				engin.search(in);
				System.out.println(ca.getFiles());
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}
	}
	private Set<Action> actions;
	private Filter filter;
	private ExtFilter zipFilter = new ExtFilter("zip,jar");
	public void search(String rootDir) {
		SysSearchEngin se = new SysSearchEngin();
		se.setFilter(zipFilter);
		se.addAction(new ZipAction());
	}
	 
}
