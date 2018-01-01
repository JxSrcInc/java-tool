package jxsource.util.io.filter;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Enumeration;
//import javax.swing.filechooser.FileFilter;
import java.io.FileFilter;

public class NameFileFilter implements FileFilter{

	String[] toMatch;
	public static String DefaultSeparator = ",";
	boolean caseDepend = false;
	OrFilter orFilter;
	
	public NameFileFilter(String strToMatch)
	{	this(strToMatch,true);
	}

	public NameFileFilter(String strToMatch, boolean isSingle)
	{	if(isSingle)
		{	toMatch = new String[] {strToMatch};
		} else
		{ StringTokenizer st = new StringTokenizer(strToMatch, DefaultSeparator);
			toMatch = new String[st.countTokens()];
			for(int i=0; i<toMatch.length; i++)
			{	toMatch[i] = st.nextToken().trim().toLowerCase();
			}
		}
		init();
	}

	public NameFileFilter(boolean caseDepend, String strToMatch)
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
		init();
	}

	public NameFileFilter(boolean caseDepend, String[] toMatch)
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
		init();
	}
	
	public NameFileFilter(String[] toMatch)
	{
		this(false, toMatch);
	}

	protected void init()
	{
		orFilter = new OrFilter(new FileFilter[] {new DirectoryFilter(), new NameFilter(caseDepend, toMatch)});
	}
	
	public boolean accept(File f) {
		return orFilter.accept(f);
/*
		if(f != null) {
			String path = f.getName();
			if(!caseDepend)
			{
				path = path.toLowerCase();
				for(int i=0; i<toMatch.length; i++)
				{
					if(path.indexOf(toMatch[i]) > -1)
						return true;
				}
			}
		}
		return false;
*/
	}

}
