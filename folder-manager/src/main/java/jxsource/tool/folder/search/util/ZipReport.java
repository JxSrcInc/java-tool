package jxsource.tool.folder.search.util;

import java.util.List;

import jxsource.tool.folder.search.JFile;

public abstract class ZipReport {
	public abstract void report(String url, List<JFile> extractFiles);
}
