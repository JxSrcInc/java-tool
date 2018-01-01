package jxsource.tool.folder.search;

import java.io.File;
public class SysFile extends JFile{
	public SysFile(File file) {
		setPath(file.getPath());
		setLength(file.length());
		setDirectory(file.isDirectory());
	}
	
}
