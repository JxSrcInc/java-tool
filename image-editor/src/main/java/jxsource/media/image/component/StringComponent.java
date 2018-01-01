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

import jxsource.media.image.DemoFont;
import jxsource.media.image.ImageUtilities;
import jxsource.util.swing.FontChooser;


public class StringComponent extends ImageComponent
{	public static String strText = "Text";
	public static String strFont = "Font";
	public static String strColor = "Color";
	String strFontChooser = "Font Chooser";
	String strClose = "Close";
	String strSampleString = "Text";
	int sampleFontSize = 15;

	String text = "";
	JTextField propertyTextField = new JTextField();
	FontChooser fontChooser = new FontChooser();
	JLabel fontLabel;
	JPanel colorPanel;
	Font selectedFont;
	Color selectedColor;
	Component parent;

	JTextField value = new JTextField();
	
	public StringComponent()
	{	selectedFont = fontChooser.getSelectedFont();
		properties.put(strFont, selectedFont);
		selectedColor = Color.black;
		properties.put(strColor, selectedColor);
		propertyTextField.addCaretListener(new CaretListener()
		{	public void caretUpdate(CaretEvent ce) {
				if(!propertyTextField.getText().equals(text))
				{	String oldText = text;
					text = propertyTextField.getText();
					createImage();
					firePropertyChanged(new PropertyChangeEvent(propertyTextField,strText,oldText,text));
				}
			}
		});

		properties.put(strText, propertyTextField);
		propertyView.addPropertyToView(ImageComponent.strX);
		propertyView.addPropertyToView(ImageComponent.strY);
		propertyView.addPropertyToView(strText);
		propertyView.addPropertyToView(strFont);
		propertyView.addPropertyToView(strColor);

		createSampleImage();
	}

	// image just used to record image size
	private void createImage() {
		if(text.length() == 0)
			return;
		Rectangle d = selectedFont.getStringBounds(text, new FontRenderContext(selectedFont.getTransform(),false,false)).getBounds();
		if(	image == null || image.getWidth(null) != (int) d.getWidth() ||
			 	image.getHeight(null) != (int) d.getHeight())
		{	setBaseImage(ImageUtilities.makeBufferedImage((int) d.getWidth(), (int) d.getHeight()));
		}	
	}

	public void createSampleImage()
	{	//the method is called in super class constructor
		// so selectedFont and selectedColor have not been set
		if(sampleImage == null)
		{	sampleImage = new JLabel();
		} else
		{	if(text.length()>6)
				strSampleString = text.substring(0,6)+"..";
			else
				strSampleString = text;
			BufferedImage bi = ImageUtilities.makeBufferedImage(sampleWidth,sampleHeight);
			Font sampleFont = new Font(selectedFont.getFontName(),selectedFont.getStyle(),sampleFontSize);
			Rectangle d = sampleFont.getStringBounds(strSampleString, new FontRenderContext(sampleFont.getTransform(),false,false)).getBounds();
			Graphics2D g2 = bi.createGraphics();
			g2.setColor(Color.white);
			g2.fillRect(0,0,sampleWidth,sampleHeight);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setFont(sampleFont); 
			g2.setColor(selectedColor);
			g2.drawString(strSampleString,(sampleWidth-(int)d.getWidth())/2,(int)(d.getHeight()+d.getY())+sampleHeight/2);
			sampleImage.setIcon(new ImageIcon(bi));
		}
	}


	public void paintImage(Graphics g)
	{	if(text.length() == 0)
			return;
		Rectangle d = selectedFont.getStringBounds(text, new FontRenderContext(selectedFont.getTransform(),false,false)).getBounds();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(selectedFont); 
		g2.setColor(selectedColor);
		g2.drawString(text,_getX(),_getY()+(int)(-d.getY()));
	}

	public void removeImage(Graphics g)
	{	g.fillRect(_getX(),_getY(),_getWidth(),_getHeight());
	}


	public void propertyChange(PropertyChangeEvent event)
	{	String name = event.getPropertyName();
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
		if(name.equals(strFont))
		{	Font newFont = (Font) event.getNewValue();
			if(!selectedFont.equals(newFont)) {
				Font oldFont = selectedFont;
				selectedFont = newFont;
				createImage();
				createSampleImage();
				firePropertyChanged(new PropertyChangeEvent(this,strFont,oldFont,newFont));
			}
		} else
		{	super.propertyChange(event);//firePropertyChanged(event);
		}
	}

	public static void main(String[] args)
	{	JFrame f = new JFrame();
		f.getContentPane().add(new StringComponent());
		f.pack();
		f.setVisible(true);
	}
}
		
		
