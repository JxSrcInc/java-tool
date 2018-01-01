package jxsource.util.swing.objectview;

import java.util.*;
import java.lang.reflect.Field;

public class SimpleFieldComparator implements Comparator
{	Field field;

	public SimpleFieldComparator(Field field)
	{	this.field = field;
	}

	public boolean equals(Object obj)
	{	return (obj != null && (obj instanceof SimpleFieldComparator));
	}

	public int compare(Object o1, Object o2)
	{			Object v1 = null;
				try{ v1 = field.get(o1);} catch(Exception e) {e.printStackTrace();}
				Object v2 = null;
				try{ v2 = field.get(o2);} catch(Exception e) {e.printStackTrace();}
				
				int ret = -1; //v1 != null && v2 == null
				if( v1 != null && v2 != null)
				{	// programmer should make v1 and v2 to be the same class
					if(v1 instanceof Boolean)
					{ if(!v1.equals(v2))
						{	if(v1.equals(Boolean.TRUE))
								return 1;
							else
								return -1;
						}
						return 0;
					} else
					{	ret = ((Comparable)v1).compareTo(v2);
					}
				} 
				if(v1 == null && v2 == null)
				{ ret = 0;
				} 	
				if(v1 == null && v2 != null)
				{ ret = 1;
				} 	
				return ret;
						
	}
}
