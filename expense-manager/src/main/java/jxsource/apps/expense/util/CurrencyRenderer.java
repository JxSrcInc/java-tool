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

public class CurrencyRenderer implements TableCellRenderer
{
	private JLabel text = new JLabel();
	private JPanel pane = new JPanel();
	private static NumberFormat format = NumberFormat.getCurrencyInstance();

  public Component getTableCellRendererComponent(JTable table,
      Object value, boolean isSelected, boolean hasFocus,
      int row, int column)
   {	double dValue = 0;

			if(isSelected) {
				text.setForeground(table.getSelectionForeground());
				if(!hasFocus) {
					pane.setBackground(table.getSelectionBackground());
					text.setBackground(table.getSelectionBackground());
				} else {
					pane.setBackground(table.getBackground());
					text.setBackground(table.getBackground());
				}
			} else {
				text.setForeground(table.getForeground());
				pane.setBackground(table.getBackground());
				text.setBackground(table.getBackground());
			}
			text.setBorder(BorderFactory.createEmptyBorder());
			text.setHorizontalAlignment(SwingConstants.RIGHT);
			text.setFont(table.getFont());

			text.setText("");
			if(value != null)
			{	if(value.getClass() == Float.class)
				{	text.setText(format.format(value));
					if(((Float)value).floatValue() < 0)
					{ text.setForeground(Color.red);
					}
				} else
				if(value.getClass() == Double.class)
				{	text.setText(format.format(value));
					if(((Double)value).doubleValue() < 0)
					{ text.setForeground(Color.red);
					}
				} 
			}

			pane.setLayout(new GridLayout(1,2,2,0));
			pane.add(text);
			return pane;
	 }
}

