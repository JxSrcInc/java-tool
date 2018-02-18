package jxsource.playground.system;

import java.util.Properties;

public class SystemProperties {
	public static void main(String...args) {
		Properties p = System.getProperties();
		for(Object key: p.keySet()) {
			System.out.println(key+" = "+p.get(key));
		}
		System.out.println(System.getProperty("file.separator"));
	}
}
