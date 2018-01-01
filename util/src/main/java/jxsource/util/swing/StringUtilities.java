package jxsource.util.swing;

import java.text.*;
import java.util.*;
import java.awt.Component;
import javax.swing.*;

public class StringUtilities extends Object
{
	public static String getLastToken(String src, String delimiter)
	{ int ind = src.lastIndexOf(delimiter);
		if(ind < 0) return src;
		return src.substring(ind+1);
	}

	public static String getFirstToken(String src, String delimiter)
	{ int ind = src.indexOf(delimiter);
		if(ind < 0) return src;
		return src.substring(0,ind);
	}

	public static String getSecondToken(String src, String delimiter)
	{ int ind = src.indexOf(delimiter);
		if(ind < 0) return null;
		return src.substring(ind+delimiter.length());
	}

	public static String getNTokens(String src, String delimiter, int n)
	{ int ind = 0;
		int count = 0;
		while(ind < src.length() && count < n)
		{	if(src.startsWith(delimiter,ind)) count++;
			ind++;
		}
		if(ind >= src.length()) return src;
		return src.substring(0,ind-1);
	}

	// ISOControls: 0-31, 127-159
	public static String removeISOControlChar(String inStr)
	{	String str = "";
		for(int i=0; i<inStr.length(); i++)
		{	if(!Character.isISOControl(inStr.charAt(i)))
			{	str += inStr.charAt(i);
			}
		}
		return str;
	}

	public static String removeUnprintableASCIIChar(String inStr)
	{	String str = "";
		for(int i=0; i<inStr.length(); i++)
		{	int c = (int) inStr.charAt(i);
			if(31 < c && c < 127)
			{	str += inStr.charAt(i);
			}
		}
		return str;
	}

	public static String removeSpaceChar(String inStr)
	{	String str = "";
		for(int i=0; i<inStr.length(); i++)
		{	if(!Character.isSpaceChar(inStr.charAt(i)))
			{	str += inStr.charAt(i);
			}
		}
		return str;
	}

	public static String displayISOControlChar(String inStr, String escStr)
	{	String str = "";
		for(int i=0; i<inStr.length(); i++)
		{	if(Character.isISOControl(inStr.charAt(i)))
			{ //System.out.println((int)inStr.charAt(i));
				str += escStr+Integer.toHexString((int) inStr.charAt(i));
			} else
			{	str += inStr.charAt(i);
			}
		}
		return str;
	}

	public static String replaceString(String srcStr, String oldStr, String newStr)
	{	StringBuffer buf = new StringBuffer();
		int pos = 0;
		for(int i=0; i<srcStr.length()-oldStr.length(); i++)
		{	if(srcStr.substring(i,i+oldStr.length()).equals(oldStr))
			{	buf.append(newStr);
				i += oldStr.length()-1;
			} else
			{	buf.append(srcStr.charAt(i));
			}
			pos = i+1;
//System.out.println("--> "+(srcStr.length()-oldStr.length())+":"+i+"\t"+buf);
		}
//System.out.println("--> "+(srcStr.substring(pos)));
//System.out.println("? "+srcStr.substring(pos)+","+(srcStr.substring(pos).length()-oldStr.length()));
		if((srcStr.substring(pos).lastIndexOf(oldStr) == srcStr.substring(pos).length()-oldStr.length()) ? true : false)
		{ buf.append(newStr);
		} else
		{ buf.append(srcStr.substring(pos));
		}
//System.out.println("--> "+srcStr+","+oldStr+","+newStr);
//System.out.println("<-- "+buf);
		return buf.toString();
	}

	public static void main(String args[])
	{

String src = "++&&";
System.out.println(src.length()+"\t"+src);
		String old = "+";
System.out.println(old.length()+"\t"+old);
		String news = "%2B";
System.out.println(news.length()+"\t"+news);
System.out.println(src=StringUtilities.replaceString(src,old,news));
 old = "&";
System.out.println(old.length()+"\t"+old);
		 news = "%26";
System.out.println(news.length()+"\t"+news);
		System.out.println(StringUtilities.replaceString(src,old,news));
/*
String src = "file:/C:/Prod/Develop/JX%20Open/classes/";
System.out.println(src.length()+"\t"+src);
		String old = "%20";
System.out.println(old.length()+"\t"+old);
		String news = " ";
System.out.println(news.length()+"\t"+news);
		System.out.println(StringUtilities.replaceString(src,old,news));
*/
	}

}