package jxsource.util.swing;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

import org.powermock.core.ListMap;

import java.beans.*;
import java.awt.event.*;

public class FontChooser extends JPanel implements PropertyChangeListener
{	JComboBox listName;
	JTextField textSize;
	JCheckBox checkboxPlain, checkboxBold, checkboxItalic;
	protected PropertyView propertyView;
	protected ListMap properties;
	String strFontName = "Font Name";
	String strFontSize = "Font Size";
	String strPlain = "Plain";
	String strBold = "Bold";
	String strItalic = "Italic";
	boolean bold = false;
	boolean italic = false;
	int size = 30;
	int style;

	public FontChooser()
	{	String fontList[] =
          GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		listName = new JComboBox(fontList);
		style = Font.PLAIN;
		properties = new ListMap();
		properties.put(strFontName,listName);
		properties.put(strFontSize,new Integer(size));
		properties.put(strBold,Boolean.FALSE);
		properties.put(strItalic,Boolean.FALSE);
		propertyView = new PropertyView(properties);
		propertyView.addPropertyChangeListener(this);
		propertyView.addPropertyToView(strFontName);
		propertyView.addPropertyToView(strFontSize);
		propertyView.addPropertyToView(strBold);
		propertyView.addPropertyToView(strItalic);
		
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5,3,5,3));
		add(propertyView, BorderLayout.CENTER);
	}

	public void propertyChange(PropertyChangeEvent event)
	{ String name = event.getPropertyName();
		if(name.equals(strFontSize))
		{	size = ((Integer)event.getNewValue()).intValue();
		} else
		if(name.equals(strBold))
		{	bold = ((Boolean)event.getNewValue()).booleanValue();
			setStyle();
		} else
		if(name.equals(strItalic))
		{	italic = ((Boolean)event.getNewValue()).booleanValue();
			setStyle();
		}
	}

	private void setStyle()
	{	if(bold && italic)
			style = Font.BOLD | Font.ITALIC;
		else
		if(bold)
			style = Font.BOLD;
		else
		if(italic)
			style = Font.ITALIC;
		else
			style = Font.PLAIN;
	}

	public String toString()
	{	return getFontName()+","+getStyleString()+","+getSizeString();
	}

	public Font getSelectedFont()
	{	return new Font((String)listName.getSelectedItem(),style,size);
	}

	public String getFontName()
	{	return (String) listName.getSelectedItem();
	}

	public String getStyleString()
	{	if(bold && italic)
			return strBold + "|" + strItalic;
		else
		if(bold)
			return strBold;
		else
		if(italic)
			return strItalic;
		else
			return strPlain;
	}
	
	public String getSizeString()
	{	return Integer.toString(size);
	}
		
	public static void main(String[] args)
	{	JFrame frame = new JFrame();
		JDialog f = new JDialog(frame,true);
		f.setSize(300,200);
//		f.setModal(true);
		FontChooser fc = new FontChooser();
		f.getContentPane().add(fc);
		f.show();
		System.out.println(fc.getSelectedFont());
	}
}
