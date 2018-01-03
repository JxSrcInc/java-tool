package jxsource.tool.folder.search.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.zip.ZipInputStream;

import jxsource.tool.folder.search.JFile;
import jxsource.tool.folder.search.SysSearchEngin;
import jxsource.tool.folder.search.ZipSearchEngin;
import jxsource.tool.folder.search.action.Action;
import jxsource.tool.folder.search.action.CollectionAction;
import jxsource.tool.folder.search.action.ZipExtractAction;
import jxsource.tool.folder.search.filter.ExtFilter;
import jxsource.tool.folder.search.filter.Filter;
import jxsource.tool.folder.search.filter.ZipFilter;
import jxsource.tool.folder.search.util.ZipReportPrinter;

public class ZipSearchTemplate implements Template{
	
	private File rootDir;
	private ZipFilter zipFilter = new ZipFilter();
	private SysSearchEngin se;
	private ZipSearchTemplate(File rootDir) {
		se = new SysSearchEngin();
		se.setFilter(zipFilter);
		
		se.addAction(new ZipExtractAction()
				.setFilter(new ExtFilter("class"))
				.setReport(new ZipReportPrinter()));
		this.rootDir = rootDir;
	}
	public void run() {
		se.search(rootDir);
	}
	
	public static ZipSearchTemplateBuilder getBuilder() {
		return new ZipSearchTemplateBuilder();
	}
	static class ZipSearchTemplateBuilder {
		File rootDir = new File(System.getProperty("user.dir"));
		public ZipSearchTemplateBuilder setRootDir(File rootDir) {
			this.rootDir = rootDir;
			return this;
		}
		public ZipSearchTemplate build() {
			return new ZipSearchTemplate(rootDir);
		}
	}
}
