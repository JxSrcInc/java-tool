package jxsource.apps.expense.util;

import java.util.*;

public class CSVFileParser
{
	static final char d = ',';
	static final char q = '"';

	public static String[] parse(String src)
	{	int len = src.length();
		if(len == 0)
			return new String[0];
		Vector v = new Vector();
		int curr = 0;
		int init = 0;
		boolean quot = false;
		while(curr < len)
		{	char c = src.charAt(curr);
			switch(c)
			{	case q:
					if(!quot)
					{	quot = true;
					} else
					{ 	v.add(src.substring(init,curr));
						quot = false;
					}
					curr++;
					init = curr;
					break;
				case d:
					if(!quot)
					{	if(curr == 0)
						{ // first char of the string
							v.add("");
						} else
						if(src.charAt(curr-1) != q)
						{ 	v.add(src.substring(init,curr));
						}
						init = curr+1;
					}
					curr++;
					break;
				default:
					curr++;
			}
		}
		if(src.charAt(curr-1) != q)
			v.add(src.substring(init,curr));
		return (String[]) v.toArray(new String[0]);
	}

	public static void main(String[] args)
	{	String[] s = CSVFileParser.parse(args[0]);
		System.out.println(s.length);
		for(int i=0; i<s.length; i++)
			System.out.println(i+", "+s[i]);
	}
}
