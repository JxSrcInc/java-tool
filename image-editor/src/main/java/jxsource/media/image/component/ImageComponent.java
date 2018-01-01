package jxsource.media.image.component;

import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.*;
import jxsource.util.swing.PropertyView;
import jxsource.util.collective.ListMap;
import java.beans.*;
import java.util.HashSet;
import java.util.Iterator;
import jxsource.media.image.ImageUtilities;

public class ImageComponent extends JPanel implements PropertyChangeListener
{	public static String strX = "ImageX";
	public static String strY = "ImageY";
	public static String strWidth = "ImageWidth";
	public static String strHeight = "ImageHeight";
	public static String strDistortion = "Distortion";
	public static String strSelectImageX = "SelectX";
	public static String strSelectImageY = "SelectY";
	public static String strSelectImageWidth = "SelectWidth";
	public static String strSelectImageHeight = "SelectHeight";

	protected BufferedImage image; // image source, image to be changed
	BufferedImage selectedImage; // selected subimage of image
	Image adjustImage; // final image
	protected JLabel sampleImage;
	protected ListMap properties;
	protected int sampleWidth = 80;
	protected int sampleHeight = 80;
	boolean distortion = false;
	String strActive = "Active";
	String strShowProperty = "Show Property";
	String strResetImage = "Reset Image";
	protected PropertyView propertyView;
	HashSet listeners;
	JCheckBox checkboxActive;
	JCheckBox checkboxShowProperty;
	boolean active;
	ImageComponent me;
	JPanel propertyPanel;

	public void finalize()
	{	propertyView.finalize();
		if(properties != null)
			properties.clear();
		properties = null;
		if(listeners != null)
			listeners.clear();
		listeners = null;
		sampleImage = null;
		adjustImage = null;
		image = null;
	}

	public ImageComponent()
	{	this(null);
	}

	public ImageComponent(Image image)
	{	super();
		me = this;
		properties = new ListMap();
		if(image != null)
			init(ImageUtilities.makeBufferedImage(image));
		else
			init(ImageUtilities.makeBufferedImage(sampleWidth, sampleHeight));
		listeners = new HashSet();
		setBorder(BorderFactory.createLineBorder(Color.black));
		setLayout(new BorderLayout());
		JPanel imagePanel = new JPanel(new BorderLayout());
		imagePanel.setBorder(new EmptyBorder(5,5,5,5));
		imagePanel.add(sampleImage,BorderLayout.CENTER);
		checkboxActive = new JCheckBox(strActive, active = true);
		checkboxActive.addItemListener(new ItemListener()
		{	public void itemStateChanged(ItemEvent e)
			{	if(e.getStateChange() == ItemEvent.SELECTED)
				{	active = true;
					firePropertyChanged(new PropertyChangeEvent(me,strActive,Boolean.FALSE,Boolean.TRUE));
				}	else
				{	active = false;
					firePropertyChanged(new PropertyChangeEvent(me,strActive,Boolean.TRUE,Boolean.FALSE));
				}
			}
		});
		checkboxShowProperty = new JCheckBox(strShowProperty, active = true);
		checkboxShowProperty.addItemListener(new ItemListener()
		{	public void itemStateChanged(ItemEvent e)
			{	if(e.getStateChange() == ItemEvent.SELECTED)
				{	propertyPanel.setVisible(true);
				}	else
				{	propertyPanel.setVisible(false);
				}
			}
		});
		JButton resetButton = new JButton(strResetImage);
		resetButton.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent ae)
			{	// right now, listener ImageEditPanal only use the event as a signal
				setBaseImage(getBaseImage());
				firePropertyChanged(new PropertyChangeEvent(me,"",new Object(), new Object()));
			}
		});
			
		JPanel actionPanel = new JPanel(new GridLayout(0,1));
		actionPanel.setBorder(new EmptyBorder(0,3,0,0));
		actionPanel.add(checkboxActive);
		actionPanel.add(checkboxShowProperty);
		actionPanel.add(resetButton);
		imagePanel.add(actionPanel,BorderLayout.EAST);
		add(imagePanel,BorderLayout.NORTH);

		propertyView = new PropertyView(properties);
		propertyView.addPropertyChangeListener(this);
		propertyPanel = new JPanel(new BorderLayout());
		propertyPanel.setBorder(new EmptyBorder(5,3,5,5));
		propertyPanel.add(propertyView, BorderLayout.CENTER);
		add(propertyPanel, BorderLayout.CENTER);

	}

	public BufferedImage getBaseImage()
	{	return image;
	}

	public void setBaseImage(BufferedImage image)
	{	
				this.image = image;
				selectedImage = image;
				adjustImage = image;
				createSampleImage();
				properties.put(strWidth, new Integer(image.getWidth(null)));
				properties.put(strHeight, new Integer(image.getHeight(null)));
				properties.put(strSelectImageX, new Integer(0));
				properties.put(strSelectImageY, new Integer(0));
				properties.put(strSelectImageWidth, new Integer(image.getWidth(null)));
				properties.put(strSelectImageHeight, new Integer(image.getHeight(null)));
				if(propertyView != null) // if called from <init>, propertyVeiw will be update in <init>
					propertyView.updateView();
	}

	public void init(BufferedImage bi)
	{	properties.put(strX, new Integer(0));
		properties.put(strY, new Integer(0));
		properties.put(strDistortion, Boolean.FALSE);
		setBaseImage(bi);
	}

	public boolean isActive()
	{ return active;
	}

	// implement PropertyChangeListener
	public void propertyChange(PropertyChangeEvent pce)
	{	String name = pce.getPropertyName();
		if(name.equals(strWidth))
		{	int newWidth = ((Integer)pce.getNewValue()).intValue();
			if(newWidth > 0)
			{	int newHeight = adjustImage.getHeight(null);
				if(!((Boolean)properties.get(strDistortion)).booleanValue())
				{	int w = selectedImage.getWidth(null);
					int h = selectedImage.getHeight(null);
					newHeight = h * newWidth / w;
					properties.put(strHeight, new Integer(newHeight));
				}
				adjustImage(newWidth, newHeight);
			} else
			{	properties.put(name,pce.getOldValue());
			}
			propertyView.updateView();
		} else
		if(name.equals(strHeight))
		{	int newHeight = ((Integer)pce.getNewValue()).intValue();
			if(newHeight > 0)
			{	int newWidth = adjustImage.getWidth(null);
				if(!((Boolean)properties.get(strDistortion)).booleanValue())
				{	int w = selectedImage.getWidth(null);
					int h = selectedImage.getHeight(null);
					newWidth = w * newHeight / h;
					properties.put(strWidth, new Integer(newWidth));
				}
				adjustImage(newWidth, newHeight);
			} else
			{	properties.put(name,pce.getOldValue());
			}			
			propertyView.updateView();
		} else
		if(name.equals(strSelectImageX))
		{	int x = ((Integer)pce.getNewValue()).intValue();
			if(x < image.getWidth(null) && x >= 0)
			{	properties.put(name,pce.getNewValue());
				int newSelectImageWidth = Math.min(getSelectImageWidth(),image.getWidth(null)-x);
				properties.put(strSelectImageWidth, new Integer(newSelectImageWidth));
				selectImage();
				properties.put(strWidth,new Integer(newSelectImageWidth));
				properties.put(strHeight,new Integer(getSelectImageHeight()));
				adjustImage(newSelectImageWidth,getSelectImageHeight());
			} else
			{	properties.put(name,pce.getOldValue());
			}
			propertyView.updateView();
		}	else
		if(name.equals(strSelectImageY))
		{	int y = ((Integer)pce.getNewValue()).intValue();
			if(y < image.getHeight(null) && y >= 0)
			{	properties.put(name,pce.getNewValue());
				int newSelectImageHeight = Math.min(getSelectImageHeight(),image.getHeight(null)-y);
				properties.put(strSelectImageHeight, new Integer(newSelectImageHeight));
				selectImage();
				properties.put(strHeight,new Integer(newSelectImageHeight));
				properties.put(strWidth,new Integer(getSelectImageWidth()));
				adjustImage(getSelectImageWidth(), newSelectImageHeight);
			} else
			{	properties.put(name,pce.getOldValue());
			}
			propertyView.updateView();
		}	else
		if(name.equals(strSelectImageWidth))
		{	int newSelectImageWidth = ((Integer)pce.getNewValue()).intValue();
			if(newSelectImageWidth <= image.getWidth(null)-getSelectImageX() && newSelectImageWidth > 0)
			{	properties.put(name,pce.getNewValue());
				selectImage();
				properties.put(strWidth,new Integer(newSelectImageWidth));
				properties.put(strHeight,new Integer(getSelectImageHeight()));
				adjustImage(newSelectImageWidth,getSelectImageHeight());
			} else
			{	properties.put(name,pce.getOldValue());
			}
			propertyView.updateView();
		}	else
		if(name.equals(strSelectImageHeight))
		{	int newSelectImageHeight = ((Integer)pce.getNewValue()).intValue();
			if(newSelectImageHeight <= image.getHeight(null)-getSelectImageY() && newSelectImageHeight > 0)
			{	properties.put(name,pce.getNewValue());
				selectImage();
				properties.put(strWidth,new Integer(getSelectImageWidth()));
				properties.put(strHeight,new Integer(newSelectImageHeight));
				adjustImage(getSelectImageWidth(),newSelectImageHeight);
			} else
			{	properties.put(name,pce.getOldValue());
			}
			propertyView.updateView();
		}	
		firePropertyChanged(pce);
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

	
	public void createSampleImage()
	{	// image will be null if derived class call constructor ImageComponent()
		if(image == null)
			return;
		Image tmpImage = null;
		try {
			tmpImage = ImageUtilities.resizeImage(image,sampleWidth,sampleHeight,null,BufferedImage.TYPE_INT_ARGB);
		} catch(Exception e)
		{	tmpImage = image.getScaledInstance(sampleWidth,sampleHeight,Image.SCALE_DEFAULT);
			ImageUtilities.waitForImage(tmpImage);
		}
		if(sampleImage == null)
			sampleImage = new JLabel();
		sampleImage.setIcon(new ImageIcon(tmpImage));
	}

	public Image getImage()
	{	return adjustImage;
	}

	private void adjustImage(int w, int h)
	{	adjustImage = selectedImage.getScaledInstance(w,h,Image.SCALE_DEFAULT);
		ImageUtilities.waitForImage(adjustImage);

	}

	private BufferedImage selectImage()
	{ 
		selectedImage = image.getSubimage(getSelectImageX(), getSelectImageY(),
																			getSelectImageWidth(), getSelectImageHeight());
		ImageUtilities.waitForImage(selectedImage);
		return selectedImage;
	}

	public void setSelectImageX(int value)
	{	properties.put(strSelectImageX, new Integer(value));
	}

	public int getSelectImageX()
	{	return ((Integer)properties.get(strSelectImageX)).intValue();
	}

	public void setSelectImageY(int value)
	{	properties.put(strSelectImageY, new Integer(value));
	}

	public int getSelectImageY()
	{	return ((Integer)properties.get(strSelectImageY)).intValue();
	}

	public void setSelectImageWidth(int value)
	{	properties.put(strSelectImageWidth, new Integer(value));
	}

	public int getSelectImageWidth()
	{	return ((Integer)properties.get(strSelectImageWidth)).intValue();
	}

	public void setSelectImageHeight(int value)
	{	properties.put(strSelectImageHeight, new Integer(value));
	}

	public int getSelectImageHeight()
	{	return ((Integer)properties.get(strSelectImageHeight)).intValue();
	}


	public void _setX(int value)
	{	properties.put(strX, new Integer(value));
	}

	public int _getX()
	{	return ((Integer)properties.get(strX)).intValue();
	}

	public void _setY(int value)
	{	properties.put(strY, new Integer(value));
	}

	public int _getY()
	{	return ((Integer)properties.get(strY)).intValue();
	}

	public void _setWidth(int value)
	{	properties.put(strWidth, new Integer(value));
	}

	public int _getWidth()
	{	return ((Integer)properties.get(strWidth)).intValue();
	}

	public void _setHeight(int value)
	{	properties.put(strHeight, new Integer(value));
	}

	public int _getHeight()
	{	return ((Integer)properties.get(strHeight)).intValue();
	}

	public void setDistortion(boolean value)
	{	properties.put(strDistortion, new Boolean(value));
	}

	public boolean isDistortion()
	{	return ((Boolean)properties.get(strDistortion)).booleanValue();
	}

	public void paintImage(Graphics g)
	{	g.drawImage(adjustImage, _getX(), _getY(), null);
	}

	public void removeImage(Graphics g)
	{	g.fillRect(_getX(),_getY(),_getWidth(),_getHeight());
	}

	public static void main(String[] args)
	{	try {
			Image image = Toolkit.getDefaultToolkit().createImage(args[0]);
			if(!ImageUtilities.waitForImage(image))
				throw new Exception("invalid path "+args[0]);
			JFrame f = new JFrame();
			Container c = f.getContentPane();
			c.add(new ImageComponent(image));
			f.pack();
			f.setVisible(true);
		}	catch(Exception e)
		{	e.printStackTrace();
		}
	}

}