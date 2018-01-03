package jxsource.tool.folder.search.util;

import java.util.List;

import jxsource.tool.folder.search.JFile;

public class ZipReportPrinter extends ZipReport {

	@Override
	public void report(String url, List<JFile> extractFiles) {
		System.out.println(url + " ------------------");
		for(JFile f: extractFiles)
		System.out.println(f);
	}

}
