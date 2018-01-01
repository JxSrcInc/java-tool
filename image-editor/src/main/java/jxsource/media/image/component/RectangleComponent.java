package jxsource.media.image.component;

import jxsource.util.collective.ListMap;
import jxsource.util.swing.PropertyView;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.beans.*;
import java.util.Iterator;
import jxsource.util.swing.SwingUtilities;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.text.*;
import java.awt.font.TextAttribute;
import java.awt.image.*;
import jxsource.media.image.ImageUtilities;

public class RectangleComponent extends ImageComponent
{	public static String strRectWidth = "RectWidth";
	public static String strRectHeight = "RectHeight";
	public static String strColor = "Color";
	String strClose = "Close";

	JPanel colorPanel;
	Color selectedColor;
	Component parent;
	JColorChooser colorChooser = new JColorChooser();
	Integer rectWidth;
	Integer rectHeight;
	
	public RectangleComponent()
	{	
		properties.put(strRectWidth, rectWidth = new Integer(100));
		properties.put(strRectHeight, rectHeight = new Integer(10));
		selectedColor = Color.black;
		properties.put(strColor, selectedColor);
		propertyView.addPropertyToView(ImageComponent.strX);
		propertyView.addPropertyToView(ImageComponent.strY);
		propertyView.addPropertyToView(strRectWidth);
		propertyView.addPropertyToView(strRectHeight);
		propertyView.addPropertyToView(strColor);
		propertyView.addPropertyChangeListener(this);
		// createImage should be last 
		createImage();
	}

	// image just used to record image size
	private void createImage() {
		BufferedImage image = ImageUtilities.makeBufferedImage(rectWidth.intValue(), rectHeight.intValue());
		Graphics2D g2 = image.createGraphics();
		g2.setColor(selectedColor);
		g2.fillRect(0,0,rectWidth.intValue(),rectHeight.intValue());
		setBaseImage(image);
	}

	public void propertyChange(PropertyChangeEvent event)
	{	String name = event.getPropertyName();
		if(name.equals(strRectWidth))
		{	Integer newWidth = (Integer) event.getNewValue();
			if(!rectWidth.equals(newWidth)) {
				Integer oldWidth = rectWidth;
				rectWidth = newWidth;
				createImage();
				createSampleImage();
				firePropertyChanged(new PropertyChangeEvent(this,strRectWidth,oldWidth,newWidth));
			}
		} else
		if(name.equals(strRectHeight))
		{	Integer newHeight = (Integer) event.getNewValue();
			if(!rectHeight.equals(newHeight)) {
				Integer oldHeight = rectHeight;
				rectHeight = newHeight;
				createImage();
				createSampleImage();
				firePropertyChanged(new PropertyChangeEvent(this,strRectHeight,oldHeight,newHeight));
			}
		} else
		if(name.equals(strColor))
		{	Color newColor = (Color) event.getNewValue();
			if(!selectedColor.equals(newColor)) {
				Color oldColor = selectedColor;
				selectedColor = newColor;
				createImage();
				createSampleImage();
				firePropertyChanged(new PropertyChangeEvent(this,strColor,oldColor,newColor));
			}
		} else
		{	super.propertyChange(event);//firePropertyChanged(event);
		}
	}

	public static void main(String[] args)
	{	JFrame f = new JFrame();
		f.getContentPane().add(new RectangleComponent());
		f.pack();
		f.setVisible(true);
	}
}
		
		
