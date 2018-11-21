package jxsource.net.proxy.server;

import java.io.File;

public class ClearLog {

	public static void clearDir(String dir) {
		clear(new File(dir));
	}
	public static void clear(File file) {
		if(!file.exists()) {
			return;
		}
		if(file.isDirectory()) {
			for(File child: file.listFiles()) {
				clear(child);
			}
		} 
		if(!file.delete()) {
			System.err.println("Failed to remove "+file);
		}
	}

}
