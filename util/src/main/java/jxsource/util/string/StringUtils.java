package jxsource.util.string;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class StringUtils {
	public static String addTabToLine(String src) {
		try {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(src.getBytes())));
		String line = "";
		String target = "";
		while((line=reader.readLine())!=null) {
			target += '\t' + line + '\n';
		}
		return target;
		} catch(IOException e) {
			return src;
		}
	}

	public static String convertBytesToString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<bytes.length; i++) {
			sb.append(Byte.toString(bytes[i])+' ');
		}
		return sb.toString().trim();
	}
}
