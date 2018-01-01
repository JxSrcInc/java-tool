package jxsource.media.image;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.awt.image.*;
import java.beans.*;
import java.util.HashSet;
import jxsource.media.image.component.ImageComponent;
import java.awt.event.*;

public class ImageEditPanel extends JPanel implements PropertyChangeListener
{	public static final String TRANSPARENT_BACKGROUND = "Transparent";
	public static final String COLOR_BACKGROUND = "Color";
	public static final String DEFAULT_BACKGROUND = "Default";
	String strPlanetColor = "Planet Color";

	Vector images;
	HashSet visibleImages;
	Color defaultBackground;
	Color background;
	boolean x;

	final int paintBackground = 0;
	final int paintImage = 1;
	int paint;

	public ImageEditPanel(Vector editImages)
	{	images = editImages;
		visibleImages = new HashSet();
		defaultBackground = getBackground();
		background = defaultBackground;
	}

	public Color getMyBackground()
	{
		return background;
	}

	public void clear()
	{	images.clear();
		visibleImages.clear();
		repaint();
	}

	public BufferedImage getImage()
	{	Dimension d = calculateSize();
		BufferedImage image = ImageUtilities.makeBufferedImage((int)d.getWidth(),(int)d.getHeight());
		Graphics2D g2 = image.createGraphics();
		if(background != null)
		{	g2.setColor(background);
			g2.fillRect(0,0,(int)d.getWidth(),(int)d.getHeight());
		}
		for(int i=0; i<images.size(); i++)
		{	ImageComponent ei = (ImageComponent)images.elementAt(i);
			if(ei.isActive())
				ei.paintImage(g2);//g2.drawImage(ei.getImage(), ei._getX(), ei._getY(), null);
		}
		return image;
	}

	public Dimension calculateSize()
	{	int width = 0;
		int height = 0;
		for(int i=0; i<images.size(); i++)
		{	ImageComponent ei = (ImageComponent) images.elementAt(i);
			if(ei.isActive())
			{	int w = ei._getX() + ei._getWidth();
				int h = ei._getY() + ei._getHeight();
				if(width < w)
					width = w;
				if(height < h)
					height = h;
			}
		}
		return new Dimension(width, height);
	}
	
	public void resize()
	{	Dimension imageSize = calculateSize();
		Dimension containerSize = getSize();
		boolean booleanResize = false;
		int newWidth = containerSize.width;
		int newHeight = containerSize.height;
		if(containerSize.width < imageSize.width)
		{	booleanResize = true;
			newWidth = imageSize.width;
		}
		if(containerSize.height < imageSize.height)
		{	booleanResize = true;
			newHeight = imageSize.height;
		}
		if(booleanResize)
		{	Dimension d = new Dimension(newWidth, newHeight);
			setSize(d);
			setPreferredSize(d);
		}
	}

	public void setImageComponent(ImageComponent imageElement)
	{ imageElement.addPropertyChangeListener(this);
		images.add(imageElement);
		validate();
		repaint();
		resize();
	}

	public void propertyChange(PropertyChangeEvent pce)
	{	x=true;
		repaint();
		Dimension d = calculateSize();
		resize();
	}

	public void update(Graphics g)
	{	
		paint(g);
	}

	public void paint(Graphics g)
	{	
		Insets insets = getInsets();
		Dimension d = getSize();
		g.setColor(background);
		g.fillRect(0,0,(int)d.getWidth(),(int)d.getHeight());
		for(int i=0; i<images.size(); i++)
		{	ImageComponent ei = (ImageComponent)images.elementAt(i);
			if(ei.isActive())
			{	//ei.paintImage(g); 
				if(!visibleImages.contains(ei))
					visibleImages.add(ei);
			} else
			{ if(visibleImages.contains(ei))
				{	ei.removeImage(g); //g.fillRect(ei._getX(),ei._getY(),ei._getWidth(),ei._getHeight());
					visibleImages.remove(ei);
				}
			}
		}
		for(int i=0; i<images.size(); i++)
		{	ImageComponent ei = (ImageComponent)images.elementAt(i);
			if(visibleImages.contains(ei))
				ei.paintImage(g);
		}
	}

	public void setImageBackground(String name)
	{ if(name.equals(TRANSPARENT_BACKGROUND) || name.equals(DEFAULT_BACKGROUND))
		{	background = defaultBackground;
		}	else
		{	final JColorChooser colorChooser = new JColorChooser();
			JColorChooser.createDialog(this,
    	     strPlanetColor, true, colorChooser,
					 new ActionListener() // OK button listener
        	 {  public void actionPerformed(ActionEvent event)
          	  { background = colorChooser.getColor();
            	}
	         },
  	       new ActionListener() // Cancel button listener
    	     {  public void actionPerformed(ActionEvent event)
      	      {  
        	    }
	         }).setVisible(true);
		}
		if(background != null)
			setBackground(background);
		else
			setBackground(defaultBackground);
	}

}