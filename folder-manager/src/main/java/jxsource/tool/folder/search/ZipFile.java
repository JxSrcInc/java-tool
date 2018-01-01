package jxsource.tool.folder.search;

import java.util.zip.ZipEntry;

public class ZipFile extends JFile{

	public ZipFile(ZipEntry zipEntry) {
		setPath(zipEntry.getName());
		setLength(zipEntry.getSize());
		setDirectory(zipEntry.isDirectory());
	}
}
