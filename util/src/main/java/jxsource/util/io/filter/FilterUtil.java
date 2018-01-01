package jxsource.util.io.filter;

import java.util.*;

public class FilterUtil {
	
	static String separator;
	static {
		separator = System.getProperty("jxsource.util.io.filter.separator");
		if(separator == null)
			separator = ",";
	}
	
	public static String[] getStringArray(String s)
	{
		StringTokenizer st = new StringTokenizer(s, separator);
		String[] strings = new String[st.countTokens()];
		for(int i=0; i< strings.length; i++)
			strings[i] = st.nextToken();
		return strings;
	}

}
