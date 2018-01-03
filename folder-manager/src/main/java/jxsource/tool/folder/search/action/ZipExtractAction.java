package jxsource.tool.folder.search.action;

import java.io.FileInputStream;
import java.util.zip.ZipInputStream;

import jxsource.tool.folder.search.JFile;
import jxsource.tool.folder.search.ZipSearchEngin;
import jxsource.tool.folder.search.filter.Filter;
import jxsource.tool.folder.search.util.Util;
import jxsource.tool.folder.search.util.ZipReport;

/**
 * called by ZipSearchEngin 
 */
public class ZipExtractAction implements Action {
	private Filter filter;
	private ZipReport report;

	/**
	 */
	public void proc(JFile f) {
		//  Because ZipSearchEngin use extFilter, the parameter can be a directory
		// Use if condition to filter those directories.
		if (Util.isArchive(f)) {
			String url = f.getPath();
			CollectionAction ca = new CollectionAction();
			try {
				ZipInputStream in = new ZipInputStream(new FileInputStream(f.getPath()));
				ZipSearchEngin engin = new ZipSearchEngin();
				engin.addAction(ca);
				engin.setFilter(filter);
				engin.search(in);
				report.report(url, ca.getFiles());
			} catch (Exception e) {
				throw new RuntimeException("Error when extracting "+url, e);
			}
		}
	}

	public Filter getFilter() {
		return filter;
	}

	public ZipExtractAction setFilter(Filter filter) {
		this.filter = filter;
		return this;
	}

	public ZipReport getReport() {
		return report;
	}

	public ZipExtractAction setReport(ZipReport report) {
		this.report = report;
		return this;
	}

}
