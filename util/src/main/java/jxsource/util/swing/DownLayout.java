package jxsource.util.swing;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.awt.*;

public class DownLayout implements LayoutManager, java.io.Serializable {

    public static final int LEFT 	= 0;
    public static final int CENTER 	= 1;
    public static final int RIGHT 	= 2;
    public static final int LEADING	= 3;
    public static final int TRAILING = 4;
    int align; 
    int newAlign; 
    int hgap;
    int vgap;

    public DownLayout() {
			this(LEADING, 5, 5);
    }

    public DownLayout(int align) {
			this(align, 5, 5);
    }

    public DownLayout(int align, int hgap, int vgap) {
			this.hgap = hgap;
			this.vgap = vgap;
      setAlignment(align);
    }

    public int getAlignment() {
			return newAlign;
    }

    public void setAlignment(int align) {
			this.newAlign = align;
      switch (align) {
				case LEADING:
      	  this.align = LEFT;
		    	break;
				case TRAILING:
          this.align = RIGHT;
			    break;
        default:
          this.align = align;
	  		  break;
        }
    }

    public int getHgap() {
			return hgap;
    }

    public void setHgap(int hgap) {
			this.hgap = hgap;
    }

    public int getVgap() {
			return vgap;
    }

    public void setVgap(int vgap) {
			this.vgap = vgap;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
				Dimension dim = new Dimension(0, 0);
				int nmembers = target.getComponentCount();

				for (int i = 0 ; i < nmembers ; i++) {
	    		Component m = target.getComponent(i);
			    if (m.isVisible()) {
						Dimension d = m.getPreferredSize();
						dim.width = Math.max(dim.width, d.width);
						if (i > 0) {
		    			dim.height += vgap;
						}
						dim.height += d.height;
			    }
				}
				Insets insets = target.getInsets();
				dim.width += insets.left + insets.right + hgap*2;
				dim.height += insets.top + insets.bottom + vgap*2;
				return dim;
      }
    }

    public Dimension minimumLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
				Dimension dim = new Dimension(0, 0);
				int nmembers = target.getComponentCount();

				for (int i = 0 ; i < nmembers ; i++) {
	    		Component m = target.getComponent(i);
			    if (m.isVisible()) {
						Dimension d = m.getMinimumSize();
						dim.width = Math.max(dim.width, d.width);
						if (i > 0) {
		  			  dim.height += vgap;
						}
						dim.height += d.height;
		    	}
				}
				Insets insets = target.getInsets();
				dim.width += insets.left + insets.right + hgap*2;
				dim.height += insets.top + insets.bottom + vgap*2;
				return dim;
    	}
    }

    private void moveComponents(Container target, int x, int y, int width, int height,
                                int rowStart, int rowEnd, boolean ltr) {
      synchronized (target.getTreeLock()) {
/*
	switch (newAlign) {
	case LEFT:
	    y += ltr ? 0 : height;
	    break;
	case CENTER:
	    y += height / 2;
	    break;
	case RIGHT:
	    y += ltr ? height : 0;
	    break;
	case LEADING:
	    break;
	case TRAILING:
	    y += height;
	    break;
	}
*/
				for (int i = rowStart ; i < rowEnd ; i++) {
				  Component m = target.getComponent(i);
				  if (m.isVisible()) {
						switch (newAlign) {
							case LEFT:
							  break;
							case CENTER:
								x += (width - m.getWidth()) / 2;
						    break;
							case RIGHT:
								x += width - m.getWidth();
						    break;
							case LEADING:
	    					break;
							case TRAILING:
								x += width - m.getWidth();
						    break;
						}
			      m.setLocation(x, y);
						y += m.getHeight() + vgap;
				  }
				}
      }
    }

  public void layoutContainer(Container target) {
    synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();
			int maxheight = target.getHeight() - (insets.top + insets.bottom + vgap*2);
			int nmembers = target.getComponentCount();
			int x = insets.left + hgap, y = 0;
			int rowh = 0, start = 0;

        boolean ltr = target.getComponentOrientation().isLeftToRight();

			for (int i = 0 ; i < nmembers ; i++) {
	    	Component m = target.getComponent(i);
	    	if (m.isVisible()) {
					Dimension d = m.getPreferredSize();
					m.setSize(d.width, d.height);

					if ((y == 0) || ((y + d.height) <= maxheight)) {
				    if (y > 0) {
							y += vgap;
		    		}
		    		y += d.height;
			    	rowh = Math.max(rowh, d.width);
					} else {
			    	moveComponents(target, x, insets.top + vgap, rowh, maxheight - y, start, i, ltr);
//						x += hgap + rowh;
			  	  y = d.height;
			    	rowh = d.width;
				    start = i;
					}
	  	  }
			}
			    	moveComponents(target, x, insets.top + vgap, rowh, maxheight - y, start, nmembers, ltr);
 		}
  }

    public String toString() {
	String str = "";
	switch (align) {
	  case LEFT:        str = ",align=left"; break;
	  case CENTER:      str = ",align=center"; break;
	  case RIGHT:       str = ",align=right"; break;
	  case LEADING:     str = ",align=leading"; break;
	  case TRAILING:    str = ",align=trailing"; break;
	}
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + str + "]";
    }


}
