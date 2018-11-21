package jxsource.net.proxy;

import java.io.FileInputStream;
import java.util.Properties;

public class AppProperties extends Properties{
	
	private static final long serialVersionUID = 1L;
	private static AppProperties me;
	public void loadAppProperties(String propertiesFile) {
		try {
			load(new FileInputStream(propertiesFile));
		} catch (Exception e) {
			throw new RuntimeException("Error when loading application properties from "+propertiesFile, e);
		}
	}
	
	public static AppProperties getInstance() {
		if(me == null) {
			me = new AppProperties();
		}
		return me;
	}

}
