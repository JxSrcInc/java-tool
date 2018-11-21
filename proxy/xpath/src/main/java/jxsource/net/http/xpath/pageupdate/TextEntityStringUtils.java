package jxsource.net.http.xpath.pageupdate;

import java.io.*;

public class TextEntityStringUtils {

	static int i = 0;
	
	public static String save(byte[] data) throws IOException{
			String filename = "src/main/resources/testdata/"+(i++)+".html";
			FileOutputStream out1 = new FileOutputStream(filename);
			out1.write(data);
			out1.flush();
			out1.close();
			return filename;
	}

}
