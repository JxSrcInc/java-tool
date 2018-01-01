package jxsource.util.collective;

import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.io.Serializable;

public class ListMap extends HashMap implements Serializable
{	static final long serialVersionUID = 0000000000000000001L;
	boolean updateFlag;
	private Vector order;

	public ListMap()
	{	super();
		order = new Vector();
		updateFlag = false;
	}

	public Object put(Object key, Object value)
	{	// add 'order != null' condition for java 1.3.1 serializable read problem
		if(!containsKey(key) && order != null)
				order.add(key);
		return super.put(key, value);
	}

	public Object remove(Object key)
	{	updateFlag = true;
		order.remove(key);
		return super.remove(key);
	}

	public void clear()
	{	super.clear();
		order.clear();
		updateFlag = true;
	}

	public Object clone()
	{	ListMap lm = new ListMap();
		for(int i=0; i<order.size(); i++)
		{	Object key = order.elementAt(i);
			lm.put(key, get(key));
		}
		return lm;
	}

	public void putAll(Map map)
	{	for(Iterator iterator=keySet().iterator(); iterator.hasNext();)
		{	Object key = iterator.next();
			if(!containsKey(key))
				order.add(key);
			super.put(key, map.get(key));
		}
	}

	private void update()
	{ order.clear();
//System.out.println("udate....................");
		updateFlag = false;
		for(Iterator iterator=keySet().iterator(); iterator.hasNext();)
		{	order.add(iterator.next());
		}
	}

	public void insert(Object key, Object value, int index)
	{	if(!containsKey(key))
			order.insertElementAt(key, index);
		super.put(key, value);
	}

	public int getKeyIndex(Object key) throws Exception
	{ for(int i=0; i<order.size(); i++)
		{	if(getKeyAt(i).equals(key))
				return i;
		}
		throw new Exception("Invalid key: "+key.toString());
	}

	// the first value found
	public int getValueIndex(Object value) throws Exception
	{ for(int i=0; i<order.size(); i++)
		{	if(getValueAt(i).equals(value))
				return i;
		}
		throw new Exception("Invalid value: "+value.toString());
	}

	public int findKeyIndex(Object key)
	{ for(int i=0; i<order.size(); i++)
		{	if(getKeyAt(i).equals(key))
				return i;
		}
		return -1;
	}

	// the first value found
	public int findValueIndex(Object value)
	{ for(int i=0; i<order.size(); i++)
		{	if(getValueAt(i).equals(value))
				return i;
		}
		return -1;
	}

	public boolean renameKey(Object oldKey, Object newKey)
	{	int i = findKeyIndex(oldKey);
		if(i > -1)
		{	Object value = remove(oldKey);
			insert(newKey, value, i);
			return true;
		}
		return false;
	}

	public Object replaceValue(Object key, Object value)
	{	int i = findKeyIndex(key);
		if(i > -1)
		{	Object oldValue = remove(key);
			insert(key, value, i);
			return oldValue;
		}
		return null;
	}

	public Object getKeyAt(int i)
	{	// removed update, because it change the order. 7/24/2004 --- What is the impact ?!
		//if(updateFlag || order.size() != size())
		//	update();
		return order.elementAt(i);
	}

	public Object getValueAt(int i)
	{ return get(getKeyAt(i));
	}

	public Object remove(int i)
	{ return remove(getKeyAt(i));
	}

	public static void main(String[] args)
	{	try	{
			ListMap lm = new ListMap();
			lm.put("A","A");
			lm.put("B","A");
			lm.put("C","A");
			for(int i=0; i<lm.size(); i++)
System.out.println(lm.getKeyAt(i));
		} catch(Exception e)
		{	e.printStackTrace();
		}
	}
}



