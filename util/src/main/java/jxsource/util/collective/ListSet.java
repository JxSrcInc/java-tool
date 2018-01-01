package jxsource.util.collective;

import java.util.Vector;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class ListSet extends HashSet implements java.io.Serializable
{	private Vector order;
	static final long serialVersionUID = 0000000000000000001L;
	boolean updateFlag;

	public ListSet()
	{	super();
		init();
	}

	public ListSet(int initialCapacity, float loadFactor)
	{	super(initialCapacity, loadFactor);
		init();
	}

	private void init()
	{	order = new Vector();
		updateFlag = false;
	}

	public boolean add(Object value)
	{	synchronized(this)
		{	return super.add(value);
		}
	}

	public boolean remove(Object value)
	{	synchronized(this)
		{	updateFlag = true;
			return super.remove(value);
		}
	}

	public void clear()
	{	synchronized(this)
		{	super.clear();
			updateFlag = true;
		}
	}

	public Object clone()
	{	synchronized(this)
		{ return super.clone();
		}
	}

	private void update()
	{ order.clear();
		updateFlag = false;
		for(Iterator iterator=iterator(); iterator.hasNext();)
		{	order.add(iterator.next());
		}
	}

	public int getIndex(Object value) throws Exception
	{ for(int i=0; i<order.size(); i++)
		{	if(getAt(i).equals(value))
				return i;
		}
		throw new Exception("Invalid Object: "+value.toString());
	}

	public Object getAt(int i)
	{	if(updateFlag || order.size() != size())
			update();
		return order.elementAt(i);
	}

}


