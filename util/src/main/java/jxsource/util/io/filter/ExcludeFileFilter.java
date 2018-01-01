package jxsource.util.io.filter;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import jxsource.util.io.filter.*;

public class ExcludeFileFilter implements FileFilter{

	String[] toMatch;
	public static String DefaultSeparator = ",";
	boolean caseDepend = false;
	OrFilter orFilter;
	
	public ExcludeFileFilter(String strToMatch)
	{	this(false, strToMatch);
	}

	public ExcludeFileFilter(boolean caseDepend, String strToMatch)
	{
		this(caseDepend, FilterUtil.getStringArray(strToMatch));
	}

	public ExcludeFileFilter(boolean caseDepend, String[] toMatch)
	{
		this.caseDepend = caseDepend;
		this.toMatch = toMatch;
		if(!caseDepend)
		{
			for(int i=0; i<toMatch.length; i++)
			{
				this.toMatch[i] = toMatch[i].toLowerCase();
			}
		}
	}
	
	public ExcludeFileFilter(String[] toMatch)
	{
		this(false, toMatch);
	}

	public boolean accept(File f) {
		String path = f.getPath();
		if(!caseDepend)
			path = path.toLowerCase();
		for(int i=0; i<toMatch.length; i++)
		{
			if(path.indexOf(toMatch[i]) > -1)
				return false;
		}
		return true;
	}

}
