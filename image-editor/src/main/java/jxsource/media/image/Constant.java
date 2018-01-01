package jxsource.media.image;

import jxsource.util.io.filter.*;

public class Constant {

	public static String HomeDir;
	public static ExtensionFileFilter ImageFileFilter = new ExtensionFileFilter(new String[] {"jpg", "gif", "jpeg"});
	public static ExtensionFileChooserFilter ImageFileChooserFilter = new ExtensionFileChooserFilter(new String[] {"jpg", "gif"});
	static {
		HomeDir = System.getProperty("user.dir");
		// Don't change jxsource.apps.AppHomeDir
		// This System property will be set by jxsource
		if(System.getProperty("jxsource.apps.AppHomeDir") != null)
			HomeDir = System.getProperty("jxsource.apps.AppHomeDir");
	}
}
