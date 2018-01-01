package jxsource.util.swing.objectview;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.text.*;
import java.text.SimpleDateFormat;

public class DateRenderer implements TableCellRenderer
{
	private JLabel text = new JLabel();
	private JPanel pane = new JPanel();
	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	public void setDateFormat(DateFormat dateFormat)
	{
		this.dateFormat = dateFormat;
	}

  public Component getTableCellRendererComponent(JTable table,
      Object value, boolean isSelected, boolean hasFocus,
      int row, int column)
   {	
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
			text.setHorizontalAlignment(SwingConstants.LEFT);
			text.setFont(table.getFont());

			text.setText("");
			if(value != null)
			{	
				text.setText(dateFormat.format((Date)value));
			}

			pane.setLayout(new GridLayout());
			pane.add(text);
			return pane;
	 }
}

