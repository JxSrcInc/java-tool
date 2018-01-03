package jxsource.tool.folder.search.util;

import jxsource.tool.folder.search.JFile;

public class Util {
	public static final String[] archiveTypes = new String[] {
			"jar", "zip"
	};
	public static boolean isArchive(JFile f) {
		for(int i=0; i<archiveTypes.length; i++) {
			if(archiveTypes[i].equals(f.getExt().toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
