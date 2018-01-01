package jxsource.util.swing;

import java.awt.Insets;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Dimension;

public class GridLayout extends java.awt.GridLayout {
	public static int EQUAL_SIZE = 0;
    int hgap;
    int vgap;
    int rows;
    int cols;
		int width;
		int height;
		boolean widthAutoAdjust;
		boolean heightAutoAdjust;

  public GridLayout() {
		this(1, 0, 0, 0, 0, 0);
  }

  public GridLayout(int rows, int cols) {
		this(rows, cols, 0, 0, 0, 0);
  }

  public GridLayout(int rows, int cols, int hgap, int vgap) {
		this(rows, cols, hgap, vgap, 0, 0);
	}

  public GridLayout(int rows, int cols, int hgap, int vgap, int width, int height) {
		super(rows, cols, hgap, vgap);
		this.rows = rows;
		this.cols = cols;
		this.hgap = hgap;
		this.vgap = vgap;
		this.width = width;
		this.height = height;
  }

	public void setRows(int rows)
	{	this.rows = rows;
		super.setRows(rows);
	}

	public void setColumns(int cols)
	{	this.cols = cols;
		super.setColumns(cols);
	}

	public int getColumns()
	{	return cols;
	}

	public void setCellWidth(int width)
	{ this.width = width;
	}

	public void setCellHeight(int height)
	{ this.height = height;
	}

	public int adjustColumnNumber(Container parent)
	{	if(width > 0)
		{	//Rectangle rec = parent.getBounds();
			Dimension rec = parent.getSize();
			Insets insets = parent.getInsets();
			cols = (((int)rec.getWidth()) - (insets.left + insets.right) + hgap)/(hgap + width);
		} 
		return cols;
	}

	public void adjustCellHeight(int containerInsideHeight)
	{	if(rows > 0)
			width = (containerInsideHeight - (rows - 1) * vgap) / rows;
	}

  public void layoutContainer(Container parent) {
    synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = rows;
/*
			int acols = adjustColumnNumber(parent);
			if(acols != cols)
			{	setColumns(acols);
			}
*/

			int ncols = cols;
			if (ncomponents == 0) {
	  	  return;
			}
//			Rectangle rec = parent.getBounds();
			Dimension rec = parent.getSize();
			if (nrows > 0) {
	  	  ncols = (ncomponents + nrows - 1) / nrows;
			} else {
	  	  nrows = (ncomponents + ncols - 1) / ncols;
			}
			
			int w = width;
			if(w < 0)
			{	w = ((int)rec.getWidth()) - (insets.left + insets.right);
				w = (w - (ncols - 1) * hgap) / ncols;
			}
			int h = height;
			if(h < 0)
			{	h = ((int)rec.getHeight()) - (insets.top + insets.bottom);
				h = (h - (nrows - 1) * vgap) / nrows;
			}
			if(w > 0 && h == 0)
			{	h = w;
			}
			else
			if(h > 0 && w == 0)
			{	w = h;
			}
//System.out.println(cols+","+rec.getWidth()+","+w+","+h);

			for (int c = 0, x = insets.left ; c < ncols ; c++, x += w + hgap) {
	  	  for (int r = 0, y = insets.top ; r < nrows ; r++, y += h + vgap) {
					int i = r * ncols + c;
					if (i < ncomponents) {
//System.out.println(i+","+x+","+y+","+w+","+h);
		  		  parent.getComponent(i).setBounds(x, y, w, h);
					}
	    	}
			}
    }
  }
    
  public String toString() {
		return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + 
	    			       ",rows=" + rows + ",cols=" + cols + ", width="+ width +", height=" + height +"]";
    }
}
