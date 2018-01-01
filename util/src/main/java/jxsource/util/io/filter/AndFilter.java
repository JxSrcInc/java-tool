package jxsource.util.io.filter;

import java.io.*;

public class AndFilter implements FileFilter{
	
	FileFilter[] filters;
	
	public AndFilter(FileFilter[] filters)
	{
		this.filters = filters;
	}
	
	public boolean accept(File f)
	{
		if(f == null)
			return false;
		
		for(int i=0; i<filters.length; i++)
		{
			if(!filters[i].accept(f))
			{
				return false;
			}
		}
		return true;
	}
}
