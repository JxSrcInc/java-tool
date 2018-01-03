package jxsource.tool.folder.search;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import jxsource.tool.folder.search.action.FilePrintAction;
import jxsource.tool.folder.search.filter.ExtFilter;

public class ZipSearchEngin extends SearchEngin {
	
	public void search(ZipInputStream zis) throws ZipException, IOException {
        ZipEntry entry;
        while((entry = zis.getNextEntry())!=null) {
        	if(entry.isDirectory()) 
        		continue;
        	ZipFile zipFile = new ZipFile(entry);
        	consum(zipFile);
        }
        zis.close();
	}

}
