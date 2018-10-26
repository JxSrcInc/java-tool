package jxsource.playground.any;

import java.io.File;

public class FileMain {
	public static void main(String...args) {
		print("./");
		print(".");
		print("pom.xml");
	}
	
	public static void print(String path) {
		System.out.println(path+" ------------");
		File f = new File(path);
		System.out.println("name: "+f.getName());
		System.out.println("path: "+f.getPath());
		System.out.println("absolute path: "+f.getAbsolutePath());
		System.out.println();
	}
}
