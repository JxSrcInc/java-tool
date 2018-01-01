package jxsource.util.swing;

import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Dimension;
import java.awt.event.*;

public class SimpleListPanel extends JPanel
{	GridLayout listLayout;
	static final int defaultCellWidth = 40;
	static final int defaultCellHeight = 40;
	static final int defaultRows = 0;
	
	protected int cellWidth, cellHeight;
	int rows;
	int hgap = 0;
	int vgap = 0;
	Insets insets = getInsets();
	final int columns = 1;
	
	public SimpleListPanel()
	{	this(	CellListPanel.defaultCellWidth,
					CellListPanel.defaultCellHeight,
					CellListPanel.defaultRows);
	}

	// columns must > 0
	public SimpleListPanel(int cellW, int cellH, int rows)
	{	this(cellW, cellH, 0, 0, rows);
	}

	public SimpleListPanel(int cellW, int cellH, int hgap, int vgap, int rows)
	{	super();
		super.setLayout(listLayout = new GridLayout(rows, columns, hgap, vgap, cellW, cellH));
		cellWidth = cellW;
		cellHeight = cellH;
		this.rows = rows;
		this.hgap = hgap;
		this.vgap = vgap;
	}

	public void setCellSize(int w, int h)
	{	setCellWidth(w);
		setCellHeight(h);
	}

	public void setCellWidth(int w)
	{	cellWidth = w;
		listLayout.setCellWidth(w);
	}

	public void setCellHeight(int h)
	{	cellHeight = h;
		listLayout.setCellHeight(h);
	}

	protected Dimension getComponentSize()
	{ Insets insets = getInsets();
		return new Dimension(cellWidth + hgap * 2 + insets.left + insets.right, 
												cellHeight * rows + vgap * Math.max(0, rows - 1) + insets.top + insets.bottom);
	}			

	public int getCellWidth()
	{	return cellWidth;
	}

	public int getWidth()
	{	Insets insets = getInsets();
		return cellWidth + hgap * 2 + insets.left + insets.right;
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
 
