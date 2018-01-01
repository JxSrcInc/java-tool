package jxsource.util.awt.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageUtil {

	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private final Component sComponent = new Component() {};
	private final MediaTracker sTracker = new MediaTracker(sComponent);
	private	int sID = 0;
	public int defaultImageType = BufferedImage.TYPE_INT_RGB;
//	
	public synchronized Image loadImage(String path) throws Exception
	{	
		Image image = toolkit.createImage(path);
		if(!waitForImage(image))
			throw new IOException("Cannot load image: "+path);
		return image;
	}

	public boolean waitForImage(Image image)
	{	int id;
		boolean ok = false;
		synchronized(sComponent)
		{	id = sID++;
		}
		sTracker.addImage(image,id);
		try
		{	sTracker.waitForID(id);
			ok = true;
		}	catch(InterruptedException ie)
		{	// return false;
		}
		if(ok && sTracker.isErrorID(id))
			ok = false;
		sTracker.removeImage(image);
		return ok;
	}
	
	/*
	 * Keep the ratio of the original width and height 
	 * and make the image size fits the window size
	 */
	public static Image fitWindow(Image image)
	{	int w = image.getWidth(null);
		int h = image.getHeight(null);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int iw = d.width;
		int ih = d.height;
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

	public static Image fullWindow(Image image)
	{
		return changeSize(image, Toolkit.getDefaultToolkit().getScreenSize());
	}

	public static Image changeSize(Image image, Dimension d)
	{
		return changeSize(image, d.width, d.height);
	}
	public static Image changeSize(Image image, int w, int h)
	{	
		if(w < 1 || h < 1)
			return image;
		else
			return image.getScaledInstance(w,h,Image.SCALE_DEFAULT);
	}


}
