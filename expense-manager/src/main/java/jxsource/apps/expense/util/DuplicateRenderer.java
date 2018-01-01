package jxsource.apps.expense.util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.Border;
import java.beans.*;
import java.text.*;

public class DuplicateRenderer implements TableCellRenderer
{
	private JLabel text = new JLabel();
	private JPanel pane = new JPanel();
	private static NumberFormat format = NumberFormat.getCurrencyInstance();

  public Component getTableCellRendererComponent(JTable table,
      Object value, boolean isSelected, boolean hasFocus,
      int row, int column)
   {	
			if(value != null && value.toString().trim().length() > 0)
				pane.setBackground(Color.red);
			else
				pane.setBackground(Color.white);
			return pane;
	 }
}

