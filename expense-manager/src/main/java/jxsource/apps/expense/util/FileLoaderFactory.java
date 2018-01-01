package jxsource.apps.expense.util;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JDialog;
import java.awt.Component;

public class FileLoaderFactory 
{	static String[] nameOfFileLoaderClass;
	static String strTitle = "FileLoader List";
	static String strMessage = "Select FileLoader for file: ";
	static JDialog dialog;

	public static void setNameOfFileLoaderClass(String[] name)
	{	nameOfFileLoaderClass = name;
	}

	public static FileLoader create(String filename) throws
		ClassNotFoundException, InstantiationException, IllegalAccessException
	{ 
		try {	
			
			return createFileLoader(System.getProperty(Constant.SystemPropertyName_FileLoader));
		} catch(Exception e) {}
			return createFileLoader(getClassName(filename));
	}

	public static String getClassName(String filename)
	{	
		// create CSV file loader list from nameOfCSVFileLoaderClass
		JList list = new JList(nameOfFileLoaderClass);
		if(nameOfFileLoaderClass.length > 0)
		{	JOptionPane pane = new JOptionPane();
			pane.setOptions(new Object[] {list});
			pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
			dialog = pane.createDialog(null, strMessage+filename);
			list.addListSelectionListener(new ListSelectionListener()
			{	public void valueChanged(ListSelectionEvent lse)
				{	if(!lse.getValueIsAdjusting())
						dialog.dispose();
				}
			});
			//dialog.show();
			dialog.setVisible(true);
			int i = list.getSelectedIndex();
			if(i >= 0)
			{	return nameOfFileLoaderClass[i];
			} 
		}
		return null;
	}

	public static FileLoader createFileLoader(String className) throws
		ClassNotFoundException, InstantiationException, IllegalAccessException
	{	Class c = Class.forName(className);
		return (FileLoader) c.newInstance();
	}

	public static void main(String[] args)
	{ try
		{	FileLoaderFactory.setNameOfFileLoaderClass(jxsource.apps.expense.Constant.nameOfCSVFileLoaderClass);
			System.out.println(FileLoaderFactory.create(args[0]));
		} catch(Exception e) {e.printStackTrace();}
	}
}
