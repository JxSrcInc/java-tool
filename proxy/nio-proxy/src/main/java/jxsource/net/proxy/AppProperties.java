package jxsource.net.proxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AppProperties extends Properties{
	
	private static AppProperties me;
	
	private AppProperties() {
		try {
			load(new FileInputStream("app.properties"));
		} catch (Exception e) {
			throw new RuntimeException("Error when loading application properties.", e);
		}
	}
	
	public static AppProperties getInstance() {
		if(me == null) {
			me = new AppProperties();
		}
		return me;
	}

}
