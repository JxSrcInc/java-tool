/*
 * This program transfers simple fields of an Object to a XML format String.
 * A simple field must be a type of
 *		int
 * 		long
 *		float
 *		double
 *		boolean
 *		ariba.util.Date
 *		String
 * The values of other types are transfered by toString() methed
 * The field cannot be modified by "private"
 * The Array type cannot be not processed.
 */

package jxsource.apps.transfer.objectxml;

import java.lang.reflect.Field;
import org.jdom.*;
import org.jdom.output.*;
import java.io.IOException;

public class ObjectToXML
{
	public Document createDocument(Object obj)
	{	Element root = new Element("root");
		root.addContent(toXMLElement(obj));
		return new Document(root);
	}

	public Document createDocument(Object[] obj)
	{	Element root = new Element("root");
		for(int i=0; i<obj.length; i++)
			root.addContent(toXMLElement(obj[i]));
		return new Document(root);
	}

	public String toXMLString(Document doc) throws IOException
	{ XMLOutputter fmt = new XMLOutputter(Format.getPrettyFormat());
		return fmt.outputString(doc);
	}

	public String toXMLString(Object[] objs) throws IOException
	{ return toXMLString(createDocument(objs));
	}


	public Element toXMLElement(Object obj)
	{	Class c = obj.getClass();
		Element e_obj = new Element(Constant.Tag_Object);
		e_obj.setAttribute(Constant.Object_Class,c.getName());
		Field[] fields = c.getFields();
		for(int i=0; i<fields.length; i++)
		{	try
			{	Object value = fields[i].get(obj);
				// if get(obj) success, then we are here
				String type = fields[i].getType().getName();
				String name = fields[i].getName();
				Element child = null;
				if(	type.equals(Constant.Tag_char) ||
						type.equals(Constant.Tag_byte) ||
						type.equals(Constant.Tag_short) ||
						type.equals(Constant.Tag_int) ||
						type.equals(Constant.Tag_long) ||
						type.equals(Constant.Tag_float) ||
						type.equals(Constant.Tag_double) ||
						type.equals(Constant.Tag_boolean) ||
						type.equals(Constant.Tag_String))
				{	child = new Element(type);
					if(value != null)
						child.setText(value.toString());
				} else
				if(type.equals(Constant.Tag_Date))
				{	child = new Element(type);
					if(value != null)
						child.setText(Long.toString(((java.util.Date)value).getTime()));
				} else
				{ // default to Object and not recommend to use
					if(value != null)
						child = toXMLElement(value);
				}
				child.setAttribute(Constant.Field_Name, name);
				e_obj.addContent(child);
			} catch(Exception e)
			{	// if fields[i].get(obj) throws exception, then skip the field
				//e.printStackTrace();
			}
		}
		return e_obj;
	}

}

