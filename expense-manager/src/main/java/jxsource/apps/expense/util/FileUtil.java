package jxsource.apps.expense.util;

import java.io.*;

public class FileUtil
{
	public static void backFile(String f) throws IOException
	{	backFile(new File(f));
	}

	public static void backFile(File f) throws IOException
	{	 File backup = new File(f.getPath()+".bak");
			InputStream in = new FileInputStream(f);
			OutputStream out = new FileOutputStream(backup);
			byte[] buf = new byte[1028*4];
			int i = 0;
			while((i=in.read(buf)) != -1)
			{	out.write(buf,0,i);
				out.flush();
			}
			in.close();	
			out.close();
	}
}
