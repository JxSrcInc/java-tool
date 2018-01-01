package jxsource.util.io.filter;

import java.io.*;

public class FileContentFilter implements FileFilter{

	protected LineFilter contentFilter;
	
	protected FileContentFilter() {}
	
	public FileContentFilter(LineFilter filter)
	{
		contentFilter = filter;
	}
	
	public boolean accept(File f)
	{
		try
		{
			if(f != null && f.isFile())
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				String line = "";
				while((line = in.readLine()) != null)
				{
					if(contentFilter.acceptLine(line))
						return true;
				}
				in.close();
				return false;
			}
		} catch(IOException ioe)
		{
		}
		return false;
	}
}
