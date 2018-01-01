package jxsource.util.io;

import java.io.File;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Date;
//import javax.swing.filechooser.FileFilter;
import java.io.FileFilter;
import java.util.Vector;
import java.util.HashMap;

import jxsource.util.io.filter.*;

public class FileSearchUtil
{	public static String IMAGE_FILTER = "Image File";
	public static String MOVE_FILTER = "Move File";
	public static String CLASS_FILTER = "Java Class File";
	public static String JAR_FILTER = "Java Jar File";
	public static String CLASS_JAR_FILTER = "Java Class and Jar File";
	Search recursiveSearchRunnable;
	HashMap recursiveSearches = new HashMap();
	boolean includeDir;

	public InputStream recursiveSearch(File startDirectory, FileFilter[] filters) throws IOException
	{ return recursiveSearch(startDirectory, filters, false);
	}

	// not thread safe.
	public InputStream recursiveSearch(File startDirectory, FileFilter[] filters, boolean returnDir) throws IOException
	{	if(startDirectory == null || !startDirectory.isDirectory())
			throw new IOException("invalid start directory: "+startDirectory.getPath());
		PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream(out);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		recursiveSearchRunnable = new Search(startDirectory, filters, writer);
		recursiveSearchRunnable.returnDir = returnDir;
		recursiveSearchRunnable.recursive = true;
		synchronized(recursiveSearches)
		{ recursiveSearches.put(in, recursiveSearchRunnable);
		}
		new Thread(recursiveSearchRunnable).start();
		return in;
	}

	public InputStream directorySearch(File startDirectory, FileFilter[] filters) throws IOException
	{	if(startDirectory == null || !startDirectory.isDirectory())
			throw new IOException("invalid start directory: "+startDirectory.getPath());
		PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream(out);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		recursiveSearchRunnable = new Search(startDirectory, filters, writer);
		recursiveSearchRunnable.recursive = false;
		synchronized(recursiveSearches)
		{ recursiveSearches.put(in, recursiveSearchRunnable);
		}
		new Thread(recursiveSearchRunnable).start();
		return in;
	}

	// only work for latest thread
	public void stopRecursiveSearch()
	{	recursiveSearchRunnable.stop();
	}

	// thread save
	public void stopRecursiveSearch(InputStream in)
	{	Object search = recursiveSearches.remove(in);
		if(search != null)
			((Search)search).stop();
	}

	// Method to show all files which have name containing string "strToFind"
	// in a tree starting from node/directory "dir"
	public void showFileInfo(PrintStream out, String dir, String strToFind)
		throws IOException
	{
		InputStream in = recursiveSearch(new File(dir),
	       	new FileFilter[] {new NameFilter(strToFind)});
//       	new FileFilter[] {new NameFileFilter(strToFind)},true);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    String line = null;
	    while((line = reader.readLine()) != null)
	    {
			File f = new File(line);
			out.println(f.getPath());
			if(f.exists())
			{
				out.println("\t"+f.length()+"\t"+(new Date(f.lastModified())));
			} else
			{
				out.println("\t not exist");
			}
			out.println();
		}

	    in.close();
	}

	public synchronized File[] search(File startDirectory, FileFilter[] filters)
	{	Vector found = new Vector();
		if(!startDirectory.isDirectory())
			return null;
		File[] children = startDirectory.listFiles();
		for(int i=0; i<children.length; i++)
		{	if(children[i].isFile())
			{	boolean ok = true;
				if(filters != null && filters.length > 0)
				{	for(int k=0; k<filters.length; k++)
					{	if(!filters[k].accept(children[i]))
						{	ok = false;
							break;
						}
					}
				}
				if(ok)
				{	found.add(children[i]);
				}
			}
		}
		return (File[])found.toArray(new File[0]);
	}

	public synchronized File[] searchAll(File startDirectory, FileFilter[] filters)
	{	Vector found = new Vector();
		if(!startDirectory.isDirectory())
			return null;
		File[] children = startDirectory.listFiles();
		for(int i=0; i<children.length; i++)
		{	if(children[i].isFile())
			{	boolean ok = true;
				if(filters != null && filters.length > 0)
				{	for(int k=0; k<filters.length; k++)
					{	if(!filters[k].accept(children[i]))
						{	ok = false;
							break;
						}
					}
				}
				if(ok)
				{	found.add(children[i]);
				}
			} else
			if(children[i].isDirectory())
			{
				File[] f = searchAll(children[i], filters);
				for(int k=0; k<f.length; k++) 
				{
					found.add(f[k]);
				}
			}
		}
		return (File[])found.toArray(new File[0]);
	}

	public synchronized String[] searchFiles(File startDirectory, FileFilter[] filters) throws IOException
	{	Vector v = new Vector();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					recursiveSearch(startDirectory, filters)));
			String name = null;
			while((name = in.readLine()) != null)
				if(name.length() > 0) v.add(name);
		return (String[]) v.toArray(new String[0]);
	}

	public int contains(InputStream in, String toFind) throws IOException
	{	return contains(in, toFind.getBytes());
	}

	public int contains(InputStream in, byte[] toFind) throws IOException
	{ if(toFind.length > 0)
		{	byte[] buf = new byte[32];
			int bufSize = 0;
			byte[] proc = new byte[0];
			int procPos = 0;
			int srcPos = 0;
			while((bufSize=in.read(buf)) != -1)
			{	byte[] tmp = new byte[proc.length-procPos+bufSize];
				for(int i=procPos; i<proc.length; i++)
					tmp[i-procPos] = proc[i];
				for(int i=0; i<bufSize; i++)
					tmp[proc.length-procPos+i] = buf[i];
				proc = tmp;
				for(procPos=0; procPos<=proc.length-toFind.length; procPos++, srcPos++)
				{	if(proc[procPos] == toFind[0])
					{	boolean same = true;
						for(int k=1; k<toFind.length; k++)
						{
							if(proc[procPos+k] != toFind[k])
							{	same = false;
								break;
							}
						} // end for( k=0 ...
						if(same)
							return srcPos;
					}
				} // end for( procPos=0 ...
			}
		}
		return -1;
	}

	public static void main(String[] args)
	{	final FileSearchUtil fu = new FileSearchUtil();

        try {
	        FileSearchUtil sutil = new FileSearchUtil();
	        sutil.showFileInfo(System.out,args[0],args[1]);
		} catch(Exception e) {e.printStackTrace();}
		System.exit(0);
	}

	class Search implements Runnable
	{	BufferedWriter out;
		File startDirectory;
		FileFilter[] filters;
		boolean stop = false;
		public boolean recursive = true;
		public boolean returnDir = false;

		public Search(File startDirectory, FileFilter[] filters, BufferedWriter out)
		{ this.startDirectory = startDirectory;
			this.filters = filters;
			this.out = out;
		}

		public void stop()
		{	stop = true;
		}

		public void run()
		{ try
			{	recursiveSearch(startDirectory);
				out.close();
			} catch(IOException e)
			{	//	e.printStackTrace();
			}
		}

		public void recursiveSearch(File startDirectory) throws IOException
		{	File[] children = startDirectory.listFiles();
			// it is possible that listFiles returns null
			if(children == null)
				return;
			for(int i=0; i<children.length; i++)
			{	if(stop)
						return;
				if(children[i].isDirectory())
				{	if(recursive)
					{	if(returnDir)
						{
							boolean accept = true;
							if(filters != null && filters.length > 0)
							{	for(int k=0; k<filters.length; k++)
								{	if(stop)
										return;
									if(!filters[k].accept(children[i]))
									{	accept = false;
										break;
									}
								}
							}
							if(accept)
							{	String name = children[i].getPath();
								out.write(name,0,name.length());
								out.newLine();
								out.flush();
							}
//							out.write("*"+children[i].getPath());
//							out.newLine();
//							out.flush();
						}
						recursiveSearch(children[i]);
					}
				} else
				{ boolean accept = true;
					if(filters != null && filters.length > 0)
					{	for(int k=0; k<filters.length; k++)
						{	if(stop)
								return;
							if(!filters[k].accept(children[i]))
							{	accept = false;
								break;
							}
						}
					}
					if(accept)
					{	String name = children[i].getPath();
						out.write(name,0,name.length());
						out.newLine();
						out.flush();
					}
				}
			}
			//out.flush();
		}
	}
}
