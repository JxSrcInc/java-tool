package jxsource.util.swing.objectview;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class ObjectTableModel extends AbstractTableModel
{
		protected Set validSubclass = new HashSet();
    protected ArrayList objects;
    protected Class _class;
    protected Field fields[];
    protected int indexOfField[]; // from column to field's index
		protected boolean allowSort = true;
		int sortCol = -1;

    public ObjectTableModel(Class class1)
    {
        objects = new ArrayList();
        if(class1 == null)
        {
            throw new NullPointerException("Failed to create ObjectTableModel for null");
        }
        _class = class1;
//        fields = class1.getDeclaredFields();
//	change to getFields() to include super class fields.
        fields = class1.getFields();
        indexOfField = new int[fields.length];
        for(int i = 0; i < indexOfField.length; i++)
        {
            indexOfField[i] = i;
        }
    }

		public Class getDefaultClass()
		{	return _class;
		}

		public boolean getAllowSort()
		{ return allowSort;
		}

		public void setAllowSort(boolean b)
		{	allowSort = b;
		}

		public int getIndexOfObject(Object obj)
		{	return objects.indexOf(obj);
		}

		public int getIndexOfField(String name)
		{	for(int i=0; i<fields.length; i++)
				if(fields[i].getName().equals(name))
					return i;
			return -1;
		}

		public int getIndexOfColumn(String name)
		{	for(int i=0; i<getColumnCount(); i++)
				if(getColumnName(i).equals(name))
					return i;
			return -1;
		}

		public boolean setIndexOfField(String[] name)
		{	int[] index = new int[name.length];
			for(int i=0; i<name.length; i++)
			{	index[i] = getIndexOfField(name[i]);
				if(index[i] == -1)
					return false;
			}
			indexOfField = index;
			return true;
		}

		public boolean isCellEditable(int rowIndex,
                              int columnIndex)
		{ return true;
		}

		protected Field getField(int col)
		{	return fields[indexOfField[col]];
		}

		public void setValueAt(Object aValue,
                       int rowIndex,
                       int columnIndex)
		{	try{
				getField(columnIndex).set(objects.get(rowIndex), aValue);
				sortCol = -1;
			} catch(Exception e)
			{	e.printStackTrace();
			}
		}

		public boolean contains(Object obj)
		{ for(Iterator i=objects.iterator(); i.hasNext();)
			{	if(i.next().equals(obj))
					return true;
			}
			return false;
		}

		public boolean containsAtLeastOne(Object[] objs)
		{	for(int i=0; i<objs.length; i++)
				if(contains(objs[i]))
					return true;
			return false;
		}

		public boolean containsAll(Object[] objs)
		{	for(int i=0; i<objs.length; i++)
				if(!contains(objs[i]))
					return false;
			return true;
		}

		public void sort(int col)
		{	if(allowSort)
			{	if(sortCol != col)
				{	_sort(col);
					sortCol = col;
				} else
				{	revert();
				}
			}
		}

		public void _sort(int column)
		{	if(allowSort)
			{	SimpleFieldComparator comparator = new SimpleFieldComparator(getField(column));
				Object[] values = objects.toArray();
				Arrays.sort(values,comparator);
				objects.clear();
				for(int i=0; i<values.length; i++)
				{	objects.add(values[i]);
				}
			}
		}

		public void revert()
		{	Object[] values = objects.toArray();
			objects.clear();
			for(int i=values.length; i >0; i--)
				objects.add(values[i-1]);
		}


		public Class getObjectClass()
		{	return _class;
		}
/* modified to allow sub-class to be valid
    private boolean validate(Object obj)
    {	return obj != null && _class.getName().equals(obj.getClass().getName());
    }
*/
		private boolean validate(Object obj)
		{	if(obj == null)
				return false;
			Class c = obj.getClass();
			if(validSubclass.contains(c.getName()))
				return true;
			while(c != null && !_class.getName().equals(c.getName()))
			{ c = c.getSuperclass();
			}
			if(c != null)
				validSubclass.add(obj.getClass().getName());
			return (c != null);
		}

		/**
		 * This method loads data without validation
		 *
		 */
    public void load(Object obj)
    {
        objects.add(obj);
    }

    public boolean add(Object obj)
    {
        return add(objects.size(), obj);
    }

    public boolean add(int i, Object obj)
    {
        if(validate(obj) && i <= objects.size())
        {
            objects.add(i, obj);
            return true;
        } else
        {
            return false;
        }
    }

    public boolean add(int index, Object aobj[])
    {
        for(int i = 0; i < aobj.length; i++)
        {
            if(!validate(aobj[i]))
            {
                return false;
            }
        }

        for(int j = 0; j < aobj.length; j++)
        {
            objects.add(index+j, aobj[j]);
        }

        return true;
    }

    public Object remove(int i)
    {
        return objects.remove(i);
    }

    public int getRowCount()
    {
        return objects.size();
    }

    public int getColumnCount()
    {
        return indexOfField.length;
    }

    public String getColumnName(int i)
    {
        return getField(i).getName();
    }

    public Class getColumnClass(int i)
    {
        return convertToTableDisplayableClass(getField(i).getType());
    }

    public Object getObject(int i)
    {
        return objects.get(i);
    }

    public Object getValueAt(int i, int j)
    {
        Field field;
        Object obj;
        field = getField(j);
        obj = objects.get(i);
				try {
  	      return field.get(obj);
        } catch(Exception exception) {}
        return null;
    }

		public void clear()
		{	objects.clear();
		}

    Class convertToTableDisplayableClass(Class class1, Object obj)
    {
        if(class1.isInterface())
        {
            if(obj != null)
            {
                return obj.getClass();
            } else
            {
                return java.lang.Object.class;
            }
        } else
        {
            return convertToTableDisplayableClass(class1);
        }
    }

    Class convertToTableDisplayableClass(Class class1)
    {
        if(class1 == Integer.TYPE)
        {
            return java.lang.Integer.class;
        }
        if(class1 == Character.TYPE)
        {
            return java.lang.Character.class;
        }
        if(class1 == Long.TYPE)
        {
            return java.lang.Long.class;
        }
        if(class1 == Float.TYPE)
        {
            return java.lang.Float.class;
        }
        if(class1 == Double.TYPE)
        {
            return java.lang.Double.class;
        }
        if(class1 == Byte.TYPE)
        {
            return java.lang.Byte.class;
        }
        if(class1 == Boolean.TYPE)
        {
            return java.lang.Boolean.class;
        }
        if(class1.isInterface())
        {
            return java.lang.Object.class;
        } else
        {
            return class1;
        }
    }

    Object convertToTableDisplayableObject(Object obj)
    {
        Class class1 = obj.getClass();
        if(class1 == Integer.TYPE)
        {
            return (Integer)obj;
        }
        if(class1 == Character.TYPE)
        {
            return (Character)obj;
        }
        if(class1 == Long.TYPE)
        {
            return (Long)obj;
        }
        if(class1 == Float.TYPE)
        {
            return (Float)obj;
        }
        if(class1 == Double.TYPE)
        {
            return (Double)obj;
        }
        if(class1 == Byte.TYPE)
        {
            return (Byte)obj;
        }
        if(class1 == Boolean.TYPE)
        {
            return (Boolean)obj;
        } else
        {
            return obj;
        }
    }

}
