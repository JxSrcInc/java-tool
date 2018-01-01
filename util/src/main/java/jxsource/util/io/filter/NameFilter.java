package jxsource.util.io.filter;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Enumeration;
//import javax.swing.filechooser.FileFilter;
import java.io.FileFilter;

public class NameFilter implements FileFilter{

	String[] toMatch;
	public static String DefaultSeparator = ",";
	boolean caseDepend = false;
	
	public NameFilter(String strToMatch)
	{	this(true, strToMatch);
	}


	public NameFilter(boolean caseDepend, String strToMatch)
	{
		this.caseDepend = caseDepend;
		StringTokenizer st = new StringTokenizer(strToMatch, DefaultSeparator);
		toMatch = new String[st.countTokens()];
		for(int i=0; i<toMatch.length; i++)
		{	
			toMatch[i] = st.nextToken().trim();
			if(!caseDepend)
				toMatch[i] = toMatch[i].toLowerCase();
		}
	}

	public NameFilter(boolean caseDepend, String[] toMatch)
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
	
	public NameFilter(String[] toMatch)
	{
		this(false, toMatch);
	}

	public boolean accept(File f) {
		if(f != null && f.isFile()) {
			String path = f.getName();
			if(!caseDepend)
			{
				path = path.toLowerCase();
			}
			for(int i=0; i<toMatch.length; i++)
			{
				if(path.indexOf(toMatch[i]) > -1)
				{
					return true;
				}
			}
			
		}
		return false;
  }

}
