package jxsource.apps.expense.util;

import jxsource.util.swing.objectview.ObjectTableModel;

public interface FileLoader
{
	public void load(String filename, ObjectTableModel tom) throws Exception;
}
			
