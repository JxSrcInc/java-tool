package jxsource.util.swing.objectview;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
//import jxsource.util.awt.dnd.StringTransferHandler;
import jxsource.apps.transfer.objectxml.*;
import java.util.*;

public class ObjectTableTransferHandler extends ObjectStringTransferHandler {

    int addIndex = -1; //Location where items were added
    int addCount = 0;  //Number of items added
		int[] rows = null;

    protected String exportString(JComponent c) {
        rows = null;
        addCount = 0;
        addIndex = -1;

        JTable table = (JTable)c;
        rows = table.getSelectedRows();
				ObjectTableModel model = (ObjectTableModel)table.getModel();
      	Vector v = new Vector();
        for (int i = 0; i < rows.length; i++) {
					v.add(model.getObject(rows[i]));
				}
				ObjectToXML toXML = new ObjectToXML();
				String str = null;
				try
				{	str = toXML.toXMLString(v.toArray());
				} catch(Exception e)
				{	e.printStackTrace();
				}
        return str;
    }

		private void printRows(String msg)
		{	System.out.println("---> "+msg);
			for(int i=0; i<rows.length; i++)
				System.out.println("\t"+i+", "+rows[i]);
		}
    protected boolean importString(JComponent c, String str) {
        JTable target = (JTable)c;
        ObjectTableModel model = (ObjectTableModel)target.getModel();
        int index = target.getSelectedRow();

        //Prevent the user from dropping data back on itself.
        //For example, if the user is moving rows #4,#5,#6 and #7 and
        //attempts to insert the rows after row #5, this would
        //be problematic when removing the original rows.
        //So this is not allowed.
        if (rows != null && index >= rows[0] - 1 &&
              index <= rows[rows.length - 1]) {
            rows = null;
            return false;
        }
        int max = model.getRowCount();
        if (index < 0) {
            index = max;
        } else {
            index++;
            if (index > max) {
                index = max;
            }
        }
        addIndex = index;
		try {
				XMLToObject toObj = new XMLToObject();
				org.jdom.Document doc = toObj.createDocument(str);
				Object[] values = toObj.createObjects(doc);
   				addCount = values.length;
				if(model.add(index,values))
				{	index += values.length;
					model.fireTableRowsInserted(addIndex,index-1);
					return true;
				} else
				{ return false;
				}
			} catch(Exception e)
			{	// if exception, do nothing
				e.printStackTrace();
			}
			return false;
    }

    protected void cleanup(JComponent c, boolean remove) {
        JTable source = (JTable)c;
		int rowCount = source.getRowCount();
        if (remove && rows != null)
        {
            ObjectTableModel model =
                 (ObjectTableModel)source.getModel();

            //If we are moving items around in the same table, we
            //need to adjust the rows accordingly, since those
            //after the insertion point have moved.
            if (addCount < rowCount && addCount > 0)
            {
                for (int i = 0; i < rows.length; i++)
                {
                    if (rows[i] > addIndex && (rows[i]+addCount) < rowCount)
                    {
                        rows[i] += addCount;
                    }
                }
            }
			int first = rows[0];
			int last = rows[rows.length-1];
            for (int i = rows.length - 1; i >= 0; i--)
            {
                model.remove(rows[i]);
            }
			model.fireTableRowsDeleted(first,last);
        }
        rows = null;
        addCount = 0;
        addIndex = -1;
    }

 }
