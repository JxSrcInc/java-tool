package jxsource.util.swing;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Set;
import java.math.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.border.*;
import jxsource.util.swing.SwingUtilities;

public class PropertyView extends JComponent
{	String strPlanetColor = "Planet Color";
	String strClose = "Close";
	String strFontChooser = "Font Chooser";

	protected Map properties;
	// components is the UI mapping of propertie's value
	// it has the same key as properties.
	Map components;
	HashSet listeners;
	GridBagLayout gridbag;
	GridBagConstraints c;

	public void finalize()
	{ properties = null; // release only, don'e clear
		if(listeners != null)
			listeners.clear();
		listeners = null;
		components = null;
	}

	public PropertyView(Map properties)
	{	
		this.properties = properties;
		components = new HashMap(); 
		listeners = new HashSet();

		gridbag = new GridBagLayout();
		setLayout(gridbag);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
	}

	// You must use this method to select property (by key) from properites
	// and add it to view
	public boolean addPropertyToView(String key) 
	{
		if(components.containsKey(key))
			return false;
		JLabel label = new JLabel(key);
		c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
		c.fill = GridBagConstraints.NONE;      //reset to default
		c.weightx = 0.0;                       //reset to default
		label.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
		gridbag.setConstraints(label, c);
		add(label);

		c.gridwidth = GridBagConstraints.REMAINDER;     //end row
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		JComponent field = null;
		Object obj = properties.get(key); //getValueAt(i);
		if(obj instanceof String)
		{	JTextField textField = new JTextField((String) obj);
			field = textField;
			textField.addFocusListener(new TextFocusAdapter(key));
			textField.addActionListener(new TextActionListener(key));
		} else
		if(obj instanceof Number)
		{ 	JTextField textField = new JTextField(obj.toString());
			field = textField;
			textField.addFocusListener(new NumberFocusAdapter(key));
			textField.addActionListener(new NumberActionListener(key));
		} else
		if(obj instanceof Boolean)
		{ 	JCheckBox checkbox = new JCheckBox("", ((Boolean)obj).booleanValue());
			field = checkbox;
			checkbox.addItemListener(new CheckBoxItemListener(key));
		} else
		if(obj instanceof Color)
		{ 
			field = wrap(new ColorPanel(key));
		} else
		if(obj instanceof Font)
		{ 
			field = new FontView(key);
		} else
		if(obj instanceof JComponent)
		{ 
			field = (JComponent) obj;
		} else 
		if(obj == null)
		{ 
			field = new JTextField();
		} else
		{ 
			field = new JTextField(obj.toString());
			field.setEnabled(false);
		}
		components.put(key,field);
		gridbag.setConstraints(field, c);
		add(field);
		return true;
	}

	// Call this method to update the view when the value of properties changed in background
	// Note! Only String and Number can be updated
	public void updateView()
	{	for(Iterator i=components.keySet().iterator(); i.hasNext(); )
		{	Object key = i.next();
			Object value = properties.get(key);
			if(value instanceof String)
			{	((JTextField)components.get(key)).setText((String)value);
			}	else
			if(value instanceof Number)
			{	((JTextField)components.get(key)).setText(value.toString());
			}	
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener l)
	{	listeners.add(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l)
	{	listeners.remove(l);
	}

	public void firePropertyChanged(PropertyChangeEvent e)
	{	for(Iterator i=listeners.iterator(); i.hasNext();)
			((PropertyChangeListener)i.next()).propertyChange(e);
	}

	public void setEditable(String propertyName,boolean b)
	{	((JComponent)components.get(propertyName)).setEnabled(b);
	}

	class CheckBoxItemListener implements ItemListener
	{	private String propertyName;

		public CheckBoxItemListener(String name)
		{	propertyName = name;
		}

		public void itemStateChanged(ItemEvent e)
		{	JCheckBox checkBox = (JCheckBox)e.getSource();
			Object oldValue = properties.get(propertyName);
			Boolean value = Boolean.FALSE;
			if(e.getStateChange() == ItemEvent.SELECTED)
				value = Boolean.TRUE;
			properties.put(propertyName, value);
			firePropertyChanged(new PropertyChangeEvent(properties,propertyName,oldValue,value));			
		}
	}

	class TextFocusAdapter extends FocusAdapter
	{	private String propertyName;

		public TextFocusAdapter(String name)
		{	propertyName = name;
		}

		public void focusLost(FocusEvent e)
		{	String value = ((JTextField)e.getSource()).getText();
			changeString(propertyName, value);
		}
	}

	class TextActionListener implements ActionListener
	{	private String propertyName;

		public TextActionListener(String name)
		{	propertyName = name;
		}

		public void actionPerformed(ActionEvent e)
		{	String value = ((JTextField)e.getSource()).getText();
			changeString(propertyName, value);
		}
	}

	private void changeString(String key, String value)
	{		Object oldValue = properties.get(key);
			if(oldValue.equals(value))
				return;
			if(oldValue == null && value.trim().length() == 0)
				return;
			properties.put(key, value);
			firePropertyChanged(new PropertyChangeEvent(properties,key,oldValue,value));
	}

	class NumberFocusAdapter extends FocusAdapter
	{	private String propertyName;

		public NumberFocusAdapter(String name)
		{	propertyName = name;
		}

		public void focusLost(FocusEvent e)
		{	JTextField textField = (JTextField)e.getSource();
			changeNumber(propertyName, textField);
		}
	}

	class NumberActionListener implements ActionListener
	{	private String propertyName;

		public NumberActionListener(String name)
		{	propertyName = name;
		}

		public void actionPerformed(ActionEvent e)
		{	JTextField textField = (JTextField)e.getSource();
			changeNumber(propertyName, textField);
		}
	}

	private void changeNumber(String key, JTextField textField)
	{		String strValue = textField.getText();
			Object oldValue = properties.get(key);
			Object value = properties.get(key);
			if(((Integer)oldValue).toString().equals(strValue))
				return;
			try {
				if(value instanceof Integer)
				{	value = new Integer(strValue);
				} else
				if(value instanceof Long)
				{	value = new Long(strValue);
				} else
				if(value instanceof Float)
				{	value = new Float(strValue);
				} else
				if(value instanceof Double)
				{	value = new Double(strValue);
				} else
				if(value instanceof Short)
				{	value = new Short(strValue);
				} else
				if(value instanceof BigInteger)
				{	value = new BigInteger(strValue);
				} else
				if(value instanceof BigDecimal)
				{	value = new BigDecimal(strValue);
				} else
				if(value instanceof Byte)
				{	value = new Byte(strValue);
				} else
				{ throw new Exception();
				}
			} catch(Exception exc) 
			{	textField.setText(value.toString());
				return;
			}
			
			properties.put(key, value);
			firePropertyChanged(new PropertyChangeEvent(properties,key,oldValue,value));
	}

	private JComponent wrap(Component c)
	{	WrapPanel wp = new WrapPanel();
		wp.add(c, BorderLayout.CENTER);
		return wp;
	}

	class WrapPanel extends JPanel
	{	public WrapPanel()
		{	setLayout(new BorderLayout());
			setBorder(new EmptyBorder(2,2,2,2));
		}
	}

	// Color Component
	class ColorPanel extends JPanel
	{	Color selectedColor;
		JColorChooser colorChooser = new JColorChooser();
		String strColor;

		public ColorPanel(String propertyName)
		{	strColor = propertyName;
			selectedColor = (Color) properties.get(strColor);
			colorChooser.setColor(selectedColor);
			addMouseListener(new MouseAdapter()
			{	public void mouseClicked(MouseEvent me)
				{	JColorChooser.createDialog(null,
    	     strPlanetColor, true, colorChooser,
					 new ActionListener() // OK button listener
        	 {  public void actionPerformed(ActionEvent event)
          	  {  
								Color newColor = colorChooser.getColor();
								if(!newColor.equals(selectedColor)) {
									Color oldColor = selectedColor;
									selectedColor = newColor;
									setBackground(newColor);
									properties.put(strColor, selectedColor);
									firePropertyChanged(new PropertyChangeEvent(colorChooser,strColor,oldColor,newColor));
								}
            	}
	         },
  	       new ActionListener() // Cancel button listener
    	     {  public void actionPerformed(ActionEvent event)
      	      {  
        	    }
	         }).setVisible(true);
				}
			});
			setBackground(selectedColor);
		}
	}

	// Font Component
	class FontView extends JLabel
	{ String propertyName;
		Font selectedFont;
		FontChooserDialog fontChooserDialog;
		FontChooser fontChooser = new FontChooser();

		public FontView(String name)
		{	propertyName = name;
			setText(fontChooser.toString());
			addMouseListener(new MouseAdapter()
			{	public void mouseClicked(MouseEvent me)
				{	if(fontChooserDialog == null)
						fontChooserDialog = createFontChooserDialog(fontChooser);
					fontChooserDialog.show();
					Font newFont = fontChooser.getSelectedFont();
					if(!newFont.equals(selectedFont)) {
						Font oldFont = selectedFont;
						selectedFont = newFont;
						setText(fontChooser.toString());
						properties.put(propertyName,selectedFont);
						firePropertyChanged(new PropertyChangeEvent(fontChooser,propertyName,oldFont,newFont));
					}					
				}
			});
		}
	}

	FontChooserDialog createFontChooserDialog(FontChooser fontChooser) {
		Component parent = SwingUtilities.getRootContainer(this);
		FontChooserDialog fontChooserDialog = null;
		if(parent instanceof Frame)
			fontChooserDialog = new FontChooserDialog((Frame) parent, fontChooser);
		else if(parent instanceof Dialog)
			fontChooserDialog = new FontChooserDialog((Dialog) parent, fontChooser);
		else
			fontChooserDialog = new FontChooserDialog(fontChooser);
		return fontChooserDialog;
	}

	class FontChooserDialog extends JDialog
	{	int width = 300;
		int height = 200;
		FontChooser fontChooser;

		public FontChooserDialog(Frame parent, FontChooser fc)
		{	super(parent,true);
			setLocationRelativeTo(parent);
			init(fc);
		}

		public FontChooserDialog(Dialog parent, FontChooser fc)
		{	super(parent,true);
			setLocationRelativeTo(parent);
			init(fc);
		}

		public FontChooserDialog(FontChooser fc)
		{	setModal(true);
			init(fc);
		}

		void init(FontChooser fc) 
		{	setTitle(strFontChooser);
			setSize(200,160);
			Container c = getContentPane();
			c.setLayout(new BorderLayout());
			fontChooser = fc;
			if(fontChooser == null)
				fontChooser = new FontChooser();
			c.add(fontChooser, BorderLayout.CENTER);
		
			JButton closeButton = new JButton(strClose);
			closeButton.addActionListener(new ActionListener()
			{	public void actionPerformed(ActionEvent ae)
				{	dispose();
				}
			});
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(closeButton);
			c.add(buttonPanel,BorderLayout.SOUTH);
		}
	}


	public static void main(String args[])
	{	Map map = new HashMap();
		map.put("Color",Color.red);
		map.put("Font",new Font("Arial",Font.BOLD,30));
		PropertyView pv = new PropertyView(map);
		pv.addPropertyToView("Color");
		pv.addPropertyToView("Font");
	
		pv.addPropertyChangeListener(new PropertyChangeListener()
		{	public void propertyChange(PropertyChangeEvent e)
			{	System.out.println(e.getPropertyName()+","+e.getOldValue()+","+e.getNewValue());
			}
		});
		JFrame f = new JFrame();
		f.setSize(500,200);
		JPanel contentPane = (JPanel)f.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(pv, BorderLayout.CENTER);
		f.show();
	}

}
