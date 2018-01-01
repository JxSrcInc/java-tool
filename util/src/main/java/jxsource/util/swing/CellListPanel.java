package jxsource.util.swing;

import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Dimension;
import java.awt.event.*;

public class CellListPanel extends JPanel
{	GridLayout listLayout;
	static final int defaultCellWidth = 40;
	static final int defaultCellHeight = 40;
	static final int defaultColumns = 5;
	static final int defaultRows = 0;
	
	protected Container container;
	protected int containerWidth = -1;
	protected int containerHeight = -1;
	protected int cellWidth, cellHeight;
	int hgap = 0;
	int vgap = 0;
	Insets insets = getInsets();
	int columns;
	
	public CellListPanel()
	{	this(	CellListPanel.defaultCellWidth,
					CellListPanel.defaultCellHeight,
					CellListPanel.defaultRows,
					CellListPanel.defaultColumns);
	}

	// columns must > 0
	public CellListPanel(int cellW, int cellH, int rows, int columns)
	{	super();
		super.setLayout(listLayout = new GridLayout(rows, columns, hgap, vgap, cellW, cellH));
		cellWidth = cellW;
		cellHeight = cellH;
		this.columns = columns;
	}

	public void setContainer()
	{	setContainer(getParent());
	}

	public void setContainer(Container aContainer)
	{	container = aContainer;
		if(container != null)
		{	setContainerSize();
			container.addComponentListener(new ComponentAdapter()
			{	public void componentResized(ComponentEvent ce)
				{ setContainerSize();
				}
			});
		}
	}
	
	public void setCellSize(int w, int h)
	{	cellWidth = w;
		cellHeight = h;
		listLayout.setCellWidth(w);
		listLayout.setCellHeight(h);
		listLayout.setColumns(columns = getColumns());
	}

	public void setCellWidth(int w)
	{	cellWidth = w;
		listLayout.setCellWidth(w);
		listLayout.setColumns(columns = getColumns());
	}

	public void setCellHeight(int h)
	{	cellHeight = h;
		listLayout.setCellHeight(h);
	}

	protected void setContainerSize()
	{	Dimension d = container.getSize();
		containerWidth = (int) d.getWidth();
		containerHeight = (int) d.getHeight();
		listLayout.setColumns(columns = getColumns());
	}

	protected Dimension getComponentSize()
	{ if(container == null)
		{ return null;
		} else
		{	
			int cols = getColumns();
			int w = cellWidth * cols + hgap * Math.max(0, columns - 1) + insets.left + insets.right;
			int rows = getComponentCount() / cols + 1;
			int h = cellHeight * rows + vgap * Math.max(0, rows - 1) + insets.top + insets.bottom;
			return new Dimension(w, h);
		}
	}			

	protected int getColumns()
	{	if(containerWidth < 0)
			setContainerSize();
		int cols = (containerWidth - (insets.left + insets.right) + hgap)/(hgap + cellWidth);
		if(cols == 0)
			cols = 1; //defaultColumns;
		return cols;
	}

	public Dimension getSize()
	{	Dimension componentDimension = getComponentSize();
		if(componentDimension != null) {
			return componentDimension;
		} else
			return super.getSize();
	}

	public Dimension getPreferredSize()
	{	Dimension componentDimension = getComponentSize();
		if(componentDimension != null) {
			return componentDimension;
		} else
			return super.getPreferredSize();
	}

}
 
