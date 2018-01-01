package jxsource.util.io.filter;

import java.io.File;
import java.io.FileFilter;

public class DirectoryFilter implements FileFilter{

	String[] dir;
	boolean caseDepend = false;
	
	public DirectoryFilter()
	{
		dir = new String[0];
	}

	public DirectoryFilter(String[] dir)
	{	
		setExcludeDirectory(dir);
	}

	public DirectoryFilter(String[] dir, boolean caseDepend)
	{	
		setExcludeDirectory(dir, caseDepend);
	}
	
	public void setExcludeDirectory(String[] dir, boolean caseDepend)
	{	
		this.caseDepend = caseDepend;
		if(caseDepend)
		{
			for(int i=0; i<dir.length; i++)
			{
				dir[i] = dir[i].toLowerCase();
			}
		}
		this.dir = dir;
	}

	public void setExcludeDirectory(String[] dir)
	{
		this.dir = dir;
	}

	public boolean accept(File f) 
	{
		if(f == null) 
			return false;
		if(f.isDirectory())
		{
			String name = f.getName();
			if(!caseDepend)
			{
				name = name.toLowerCase();
			}
			for(int i=0; i<dir.length; i++)
			{
				if(name.equals(dir[i]) )
				{
					return false;
				}
			}
			return true;
		}
		return false;
  }

}
