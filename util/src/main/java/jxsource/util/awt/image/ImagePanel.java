package jxsource.util.awt.image;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import jxsource.util.swing.ScreenSizeWindow;

public class ImagePanel extends JPanel
{	public static final int DEFAULT = 0;
	public static final int FIT_WINDOW = 1;
	public static final int ZOOM = 2;
	public static final int FULL_WINDOW = 3;
	
	Image image; // source image
	Image adjustSizeImage;
	int displayType;
	int scale;

	public ImagePanel() 
	{	super();
		displayType = DEFAULT;
		scale = 100;
	}

	public Color getMyBackground()
	{
		return Color.white; //getBackground();
	}
	
	public void setDisplayType(int i)
	{	displayType = i;
	}

	public int getDisplayType()
	{	return displayType;
	}

	public void setScale(int i)
	{	scale = i;
	}
 
	public int getScale()
	{	return scale;
	}

	public void setImage(Image aImage)
	{	image = aImage;
		adjustImageSize();
	}
	
	/*
	 * This is a wrap function
	 */
	private int getPrintWidth()
	{ 
		int w = getWidth();
		return w;
	}
	
	/*
	 * This is a wrap function
	 */
	private int getPrintHeight()
	{ 
		int h = getHeight();
		return h;
	}

	private Dimension getComponentSize()
	{ switch(displayType)
		{	case DEFAULT:
			case FIT_WINDOW:
			case FULL_WINDOW:
				return new Dimension(getPrintWidth(),getPrintHeight());
			default:
				if(image != null)
				{	return new Dimension(image.getWidth(null), image.getHeight(null));
				} else
				{	return null;
				}
		}
	}			

	public Image getAdjustSizeImage()
	{	return adjustSizeImage;
	}

	public boolean imageUpdate(Image img, int infoflags,
			int x, int y, int width, int height)
	{	
		if((infoflags & ImageObserver.ERROR) != 0)
		{	System.err.println("Error loading image");
			return false;
		}
		if( (infoflags & ImageObserver.WIDTH) != 0 &&
				(infoflags & ImageObserver.HEIGHT) != 0)
		{	//adjustImageSize();
		}
		if((infoflags & ImageObserver.SOMEBITS) != 0)
		{	//repaint();
		}
		if((infoflags & ImageObserver.FRAMEBITS) != 0)
		{	repaint();
		}
		if((infoflags & ImageObserver.ALLBITS) != 0)
		{	//adjustImageSize();
			repaint();
			return false;
		}
		return true;
	}

	public void adjustImageSize()
	{	
		if(image != null && image.getWidth(null) > 0 && image.getHeight(null) > 0)
		{ 
			synchronized(image)
			{	switch(displayType)
				{	case DEFAULT:
					case FIT_WINDOW:
						adjustSizeImage = fitWindow(image);
						break;
					case FULL_WINDOW:
						adjustSizeImage = fullWindow(image);
						break;
					default:
						adjustSizeImage = fixSize(image);
				}
			}
			// add ImageObserver to adjustSizeImage
			adjustSizeImage.getWidth(this);
		}
	}

	private Image fixSize(Image image)
	{	Image scaledImage = image;
		if(scale != 100)
		{ int w = image.getWidth(null)*scale/100;
			int h = image.getHeight(null)*scale/100;
			scaledImage = image.getScaledInstance(w,h,Image.SCALE_DEFAULT);
		}
		// notify container size change
		setSize(getSize());
		return scaledImage;
	}

	private Image fitWindow(Image image)
	{	int w = image.getWidth(null);
		int h = image.getHeight(null);
		int iw = getPrintWidth();
		int ih = getPrintHeight();
		if(iw == 0 || ih == 0)
			return image;
		if(w > iw && h <= ih)
		{ h = iw * h / w;
			w = iw;
		} 
		else if(w <= iw && h > ih)
		{ w = ih * w / h;
			h = ih;
		} else if(w > iw && h > ih)
		{	w = ih * w / h;
			h = ih;
			// less adjust
			if(w > iw)
			{ h = iw * h / w;
				w = iw;
			}
		} else // w <= iw && h <= ih
		if(displayType == FIT_WINDOW)
		{	w = ih * w / h;
			h = ih;
			// if over adjust
			if(w > iw) 
			{ h = iw * h / w;
				w = iw;
			}
		}	
		Image scaledImage = image.getScaledInstance(w,h,Image.SCALE_DEFAULT);
		return scaledImage;
	}

	private Image fullWindow(Image image)
	{	
		int w = getPrintWidth();
		int h = getPrintHeight();
		if(w < 1 || h < 1)
			return image;
		else
			return image.getScaledInstance(w,h,Image.SCALE_SMOOTH);
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

	public Dimension getMaximumSize()
	{	Dimension componentDimension = getComponentSize();
		if(componentDimension != null) {
			return componentDimension;
		} else
			return super.getPreferredSize();
	}

	public void update(Graphics g)
	{	
		paint(g);
	}

	public void paint(Graphics g)
	{	
		if(adjustSizeImage == null)
			adjustSizeImage = image;
		if(adjustSizeImage != null) {
			Insets insets = getInsets();
			int imageWidth = adjustSizeImage.getWidth(null);
			int imageHeight = adjustSizeImage.getHeight(null);
			int printWidth = Math.max(imageWidth,getPrintWidth());
			int printHeight = Math.max(imageHeight,getPrintHeight());
			int width = (printWidth - imageWidth)/2;
			int height = (printHeight - imageHeight)/2;
			int lt_x = insets.left;
			int lt_y = insets.top;
			int rt_x = lt_x + width + imageWidth;
			int rt_y = lt_y;
			int lb_x = lt_x;
			int lb_y = lt_y + height + imageHeight;
			int rb_x = lt_x;
			int rb_y = lb_y;
				g.setColor(getMyBackground());
					if(imageWidth < printWidth && imageHeight < printHeight)
					{	g.fillRect(lt_x, lt_y, printWidth, height);
						g.fillRect(lb_x, lb_y, printWidth, height);
						g.fillRect(lt_x, lt_y+height, width, imageHeight);
						g.fillRect(rt_x, rt_y+height, width, imageHeight);
						g.drawImage(adjustSizeImage, lt_x+width, lt_y+height, getMyBackground(), null);
					} else
					if(imageWidth < printWidth)
					{	g.fillRect(lt_x, lt_y, width, printHeight);//printWidth, height);
						g.fillRect(rt_x, rt_y, width, printHeight);//printWidth, height);
						g.drawImage(adjustSizeImage, lt_x+width, lt_y, getMyBackground(), null);
					} else
					if(imageHeight < printHeight)
					{	g.fillRect(lt_x, lt_y, printWidth, height);
						g.fillRect(lb_x, lb_y, printWidth, height);
						g.drawImage(adjustSizeImage, lt_x, lt_y+height, getMyBackground(), null);
					} else
					{	g.drawImage(adjustSizeImage, insets.left, insets.top, null);
					}
		}
	}

	public void repaint()
	{	
		Graphics g = getGraphics();
		if(image != null && g != null)
			paint(g);
	}

	public static void main(String[] args)
	{		
		System.out.println(args[0]);
		JFrame f = new JFrame();
			f.setSize(400,300);
			f.addWindowListener(new WindowAdapter()
			{	public void windowClosing(WindowEvent we)
				{ System.exit(0);
				}
			});

			ImagePanel ip = new ImagePanel();
			JPanel p = new JPanel(new BorderLayout());
			p.add(ip,BorderLayout.CENTER);
//			JScrollPane container = new JScrollPane(ip);
			Container c = f.getContentPane();
			c.setLayout(new BorderLayout());
			c.add(p, BorderLayout.CENTER);
//			JButton b = new JButton("t");
//			c.add(b,BorderLayout.SOUTH);
			
			f.show();
	
			try {		
				Image image = Toolkit.getDefaultToolkit().createImage(args[0]);
//				ip.setImage(image);
				
//				image.getWidth(ip); 
				if(!new ImageUtil().waitForImage(image))
					throw new Exception("fail to load image");
				ip.setImage(image);
	System.out.println("? "+image.getWidth(null)+","+image.getHeight(null));
//					ImageIcon ii = new ImageIcon(image);
//					b.setIcon(ii);
//					b.revalidate();
//					JButton b = new JButton(ii);
//			c.add(ip, BorderLayout.CENTER);
//			f.show();

//				ip.repaint();
		} catch(Exception e) {e.printStackTrace();}

	}
}
 
