package jxsource.util.io.filter;

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
//import javax.swing.filechooser.*;
import java.util.StringTokenizer;

public class ExtensionFileFilter implements FileFilter {

  private static String TYPE_UNKNOWN = "Type Unknown";
  private static String HIDDEN_FILE = "Hidden File";
	private String noExt = "NoExt"; // no extension

  private Hashtable filters = null; // size = 0, accept all
  private String description = null;
  private String fullDescription = null;
  private boolean useExtensionsInDescription = true;

	public ExtensionFileFilter(String src, String delim)
	{	this();
		StringTokenizer st = new StringTokenizer(src,delim);
		while(st.hasMoreTokens())
		{	String ext = getExtension(st.nextToken());
			if(ext.equals("*"))
			{ filters.clear();
				break;
			} else
			{	addExtension(ext);
			}
		}
	}


  public ExtensionFileFilter() {
		this.filters = new Hashtable();
  }

  public ExtensionFileFilter(String[] filters) {
		this(filters, null);
  }

  public ExtensionFileFilter(String[] filters, String description) {
		this();
		for (int i = 0; i < filters.length; i++) {
	    // add filters one by one
	    addExtension(filters[i]);
		}
 		if(description!=null) setDescription(description);
  }

  public boolean accept(File f) {
		if(f != null) {
	    if(f.isDirectory()) {
				return true;
	    }
		if(filters.size() == 0)
			return true;
	    String extension = getExtension(f);
	    if(extension != null && filters.get(getExtension(f)) != null) {
				return true;
	    };
		}
		return false;
  }

  public String getExtension(File f) {
		if(f != null)
		{	String filename = f.getName();
	  	int i = filename.lastIndexOf('.');
	    if(i>0 && i<filename.length()-1) {
				return filename.substring(i+1).toLowerCase();
	    } else
			{ return noExt;
			}
		}
		return null;
  }

	// this function is for JXSearch.Type use only
	public String getExtension(String ext)
	{	if(ext != null)
	  { int i = ext.lastIndexOf('.');
	    if(i>=0 && i<ext.length()-1) {
				return ext.substring(i+1).toLowerCase();
	    } else
			if(ext.charAt(ext.length()-1) == '.')
			{	return noExt;
			} else
			{ return ext.toLowerCase();
			}
		}
		return null;
  }

  public void addExtension(String extension) {
		if(filters == null) {
	    filters = new Hashtable(5);
		}
		if(extension.equals(noExt))
			filters.put(extension, this);
		else
			filters.put(extension.toLowerCase(), this);
		fullDescription = null;
  }


  public String getDescription() {
		if(fullDescription == null) {
	    if(description == null || isExtensionListInDescription()) {
 				fullDescription = description==null ? "(" : description + " (";
				// build the description from the extension list
				Enumeration extensions = filters.keys();
				if(extensions != null) {
			    fullDescription += "." + (String) extensions.nextElement();
		  	  while (extensions.hasMoreElements()) {
						fullDescription += ", " + (String) extensions.nextElement();
		  	  }
				}
				fullDescription += ")";
	    } else {
				fullDescription = description;
	    }
		}
		return fullDescription;
  }

  public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
  }

  public void setExtensionListInDescription(boolean b) {
		useExtensionsInDescription = b;
		fullDescription = null;
  }

  public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
  }
}
