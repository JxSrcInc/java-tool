/**
 * This program transfers a XML format String to an Object having simple fields
 * Simple fields are defined in Constant class.
 *
 * Note:
 * The values of other types are transfered by toString() methed
 * The field cannot be modified by "private"
 * The Array type cannot be not processed.
 */

package jxsource.apps.transfer.objectxml;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;
import java.util.*;

public class XMLToObject
{
	public Document createDocument(String src) throws IOException, JDOMException
	{	SAXBuilder builder = new SAXBuilder();
		return builder.build(new ByteArrayInputStream(src.getBytes("UTF-8")));
	}

	public String toXMLString(Document doc) throws IOException
	{ XMLOutputter fmt = new XMLOutputter(Format.getPrettyFormat());
		return fmt.outputString(doc);
	}

	public Object[] createObjects(String src) throws IOException, JDOMException, NoSuchFieldException,
			ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{	return createObjects(createDocument(src));
	}

	public Object[] createObjects(Document doc) throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{	List children = doc.getRootElement().getChildren();
		Object[] objects = new Object[children.size()];
		for(int i=0; i<children.size(); i++)
		{ objects[i] = createObject(((Element)children.get(i)));
		}
		return objects;
	}

	public Object createObject(Element elem) throws ClassNotFoundException, NoSuchFieldException,
			NoSuchMethodException, InstantiationException, IllegalAccessException
	{ String name = elem.getName();
		if(!name.equals(Constant.Tag_Object))
			return null;
		String className = elem.getAttributeValue(Constant.Object_Class);
		Class c = Class.forName(className);
		Object obj = c.newInstance();
		List children = elem.getChildren();
		for(int i=0; i<children.size(); i++)
		{	Element child = (Element) children.get(i);
			String value = child.getTextTrim();
			if(value != null && value.length() > 0)
			{	Field field = c.getField(child.getAttributeValue(Constant.Field_Name));
				if(Constant.Tag_char.equals(child.getName()))
				{	field.setInt(obj,value.charAt(0));
				} else
				if(Constant.Tag_byte.equals(child.getName()))
				{	field.setInt(obj,Byte.parseByte(value));
				} else
				if(Constant.Tag_short.equals(child.getName()))
				{	field.setShort(obj,Short.parseShort(value));
				} else
				if(Constant.Tag_int.equals(child.getName()))
				{	field.setInt(obj,Integer.parseInt(value));
				} else
				if(Constant.Tag_long.equals(child.getName()))
				{	field.setLong(obj,Long.parseLong(value));
				} else
				if(Constant.Tag_float.equals(child.getName()))
				{	field.setFloat(obj,Float.parseFloat(value));
				} else
				if(Constant.Tag_double.equals(child.getName()))
				{	field.setDouble(obj,Double.parseDouble(value));
				} else
				if(Constant.Tag_Date.equals(child.getName()))
				{	field.set(obj,new Date(Long.parseLong(value)));
				} else
				if(Constant.Tag_String.equals(child.getName()))
				{	field.set(obj,value);
				} else
				if(Constant.Tag_boolean.equals(child.getName()))
				{	field.setBoolean(obj,Boolean.valueOf(value).booleanValue());
				}
			}
		}
		return obj;
	}

}

class T
{	public int i1;
	public String s;
	public java.util.Date d;
	public long l;

	public T set()
	{ i1 = 1;
		s = "sdfg";
		d = new java.util.Date(0);
		l = 100;
		return this;
	}
}
