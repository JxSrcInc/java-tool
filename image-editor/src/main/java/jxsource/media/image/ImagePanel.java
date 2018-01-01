package jxsource.media.image;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

class ImagePanel extends JPanel
{	public static final int DEFAULT = 0;
	public static final int FIT_WINDOW = 1;
	public static final int ZOOM = 2;

	Image image; // source image
	Image adjustSizeImage;
	Container container;
	int containerWidth = -1;
	int containerHeight = -1;
	int adjustSpace = 0;
	boolean fullAdjust;
	int displayType;
	int scale;
	int prvImageWidth = -1;
	int prvImageHeight = -1;
	int prvX = -1;
	int prvY = -1;

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

	public synchronized void setImage(Image aImage)
	{	image = aImage;
		if(container == null)
		{	setContainer();
		}
		adjustImageSize();
	}

	public void setContainer()
	{	setContainer(getParent());
	}

	public void setContainer(Container aContainer)
	{	container = aContainer;
		if(container != null)
		{	setContainerSize();
			container.addComponentListener(new ComponentAdapter()
			{	public void componentResized(ComponentEvent ce)
				{ prepareDisplay();
				}
			});
		}
	}
	
	private void prepareDisplay()
	{	setContainerSize();		
		if(image != null)
			adjustImageSize();
	}	

	private void setContainerSize()
	{	Dimension d = container.getSize();
		containerWidth = (int) d.getWidth();
		containerHeight = (int) d.getHeight();
	}


	private int getPrintWidth()
	{ Insets insets = getInsets();
		return containerWidth - (insets.left + insets.right) - adjustSpace;
	}

	private int getPrintHeight()
	{ Insets insets = getInsets();
		return containerHeight - (insets.top + insets.bottom) - adjustSpace;
	}

	private Dimension getComponentSize()
	{ switch(displayType)
		{	case DEFAULT:
			case FIT_WINDOW:
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
		{	System.out.println("Error loading image");
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
	{	if(image != null && image.getWidth(null) > 0 && image.getHeight(null) > 0)
		{ synchronized(image)
			{	switch(displayType)
				{	case DEFAULT:
					case FIT_WINDOW:
						adjustSizeImage = fitWindow(image);
						break;
					default:
						adjustSizeImage = fixSize(image);
				}
			}
			// add ImageObserver to adjustSizeImage
			if(scale == 100 && displayType == ZOOM)
			 repaint();
			else
				adjustSizeImage.getWidth(this);
//		adjustSizeImage.getHeight(this);
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

	public Image fitWindow(Image image)
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
/*
	public void paint(Graphics g)
	{	
		if(adjustSizeImage != null) {
			Insets insets = getInsets();

					int imageWidth = adjustSizeImage.getWidth(null);
					int imageHeight = adjustSizeImage.getHeight(null);
					int printWidth = getPrintWidth();
					int printHeight = getPrintHeight();
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
*/
	public void paint(Graphics g)
	{	
//		System.out.println(adjustSizeImage.getWidth(null));
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

			if(	imageWidth >= prvImageWidth && imageHeight >= prvImageHeight && 
					(lt_x+width) <= prvX && (lt_y+height) <= prvY &&
					prvImageWidth > 0 && prvImageHeight > 0)
			{		g.setColor(getMyBackground());
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
			} else //Double buffereing
			{		BufferedImage printImage = new BufferedImage(printWidth, printHeight, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2 = printImage.createGraphics();
					g2.setColor(getMyBackground());
					g2.fillRect(0,0,printWidth,printHeight);
					if(imageWidth < printWidth && imageHeight < printHeight)
					{	g2.fillRect(lt_x, lt_y, printWidth, height);
						g2.fillRect(lb_x, lb_y, printWidth, height);
						g2.fillRect(lt_x, lt_y+height, width, imageHeight);
						g2.fillRect(rt_x, rt_y+height, width, imageHeight);
						g2.drawImage(adjustSizeImage, lt_x+width, lt_y+height, getMyBackground(), null);
					} else
					if(imageWidth < printWidth)
					{	g2.fillRect(lt_x, lt_y, printWidth, height);
						g2.fillRect(lb_x, lb_y, printWidth, height);
						g2.drawImage(adjustSizeImage, lt_x+width, lt_y, getMyBackground(), null);
					} else
					if(imageHeight < printHeight)
					{	g2.fillRect(lt_x, lt_y, width, printHeight);
						g2.fillRect(rt_x, rt_y, width, printHeight);
						g2.drawImage(adjustSizeImage, lt_x, lt_y+height, getMyBackground(), null);
					} else
					{	g2.drawImage(adjustSizeImage, lt_x, lt_y, getMyBackground(), null);
					}
					g.drawImage(printImage, insets.left, insets.top, null);
			}
			prvImageWidth = imageWidth;
			prvImageHeight = imageHeight;
			prvX = lt_x + width;
			prvY = lt_y + height;
		} else
		{	int printWidth = getPrintWidth();
			int printHeight = getPrintHeight();
			g.setColor(getMyBackground());
			g.fillRect(0,0,printWidth,printHeight);
		}
	}

	public void repaint()
	{	if(adjustSizeImage != null)
			super.repaint();
	}

	public static void main(String[] args)
	{		JFrame f = new JFrame();
			f.setSize(400,300);
			f.addWindowListener(new WindowAdapter()
			{	public void windowClosing(WindowEvent we)
				{ System.exit(0);
				}
			});

			ImagePanel ip = new ImagePanel();
			JPanel p = new JPanel(new BorderLayout());
			p.add(ip,BorderLayout.CENTER);
			ip.setContainer(p);
//			JScrollPane container = new JScrollPane(ip);
			Container c = f.getContentPane();
			c.setLayout(new BorderLayout());
			c.add(p, BorderLayout.CENTER);
//			JButton b = new JButton("t");
//			c.add(b,BorderLayout.SOUTH);
			
			f.show();
	
			try {		
			ip.setContainer(p);
				Image image = Toolkit.getDefaultToolkit().createImage(args[0]);
//				ip.setImage(image);
				
//				image.getWidth(ip);
				if(!ImageUtilities.waitForImage(image))
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
 
