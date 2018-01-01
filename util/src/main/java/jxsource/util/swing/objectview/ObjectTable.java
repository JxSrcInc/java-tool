package jxsource.util.swing.objectview;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class ObjectTable extends JTable
{
	public ObjectTable(TableModel model)
	{
		this(model, TransferHandler.COPY_OR_MOVE);
	}
	public ObjectTable(TableModel model, int sourceAction)
	{	super(model);
		setDragEnabled(true);
		Class c = ((ObjectTableModel)model).getObjectClass();

		ObjectTableTransferHandler handler = new ObjectTableTransferHandler();
		handler.setDataFlavor(c, c.getName());

		ObjectTableHeaderTransferHandler headerHandler = new ObjectTableHeaderTransferHandler();
		headerHandler.setDataFlavor(c, c.getName());

		if( sourceAction == TransferHandler.COPY ||
				sourceAction == TransferHandler.MOVE ||
				sourceAction == TransferHandler.NONE ||
				sourceAction == TransferHandler.COPY_OR_MOVE)
		{
			handler.setSourceActions(sourceAction);
			headerHandler.setSourceActions(sourceAction);
		}

		setTransferHandler(handler);

		getTableHeader().setTransferHandler(headerHandler);
		getTableHeader().addMouseListener(new MouseAdapter()
			{	public void mouseClicked(MouseEvent event)
				{  // check for double click
					if (event.getClickCount() > 1) return;
					JTable table = ((JTableHeader)event.getSource()).getTable();
					if(table.isEditing()) table.getCellEditor().stopCellEditing();
					table.getSelectionModel().clearSelection();
					// find column of click and
					int tableColumn = table.columnAtPoint(event.getPoint());
					// translate to table model index and sort
					int modelColumn = table.convertColumnIndexToModel(tableColumn);
					ObjectTableModel model = (ObjectTableModel)table.getModel();
					model.sort(modelColumn);
					model.fireTableDataChanged();
				}
			});

	}

}


