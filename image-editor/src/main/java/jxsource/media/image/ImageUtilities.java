package jxsource.media.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtilities extends Object
{	private static final Component sComponent = new Component() {};
	private static final MediaTracker sTracker = new MediaTracker(sComponent);
	private	static int sID = 0;
	public static int defaultImageType = BufferedImage.TYPE_INT_RGB;
//	public static int imageType = defaultImageType;

	public static boolean waitForImage(Image image)
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

	public static BufferedImage makeBufferedImage(int width, int height)
	{	return makeBufferedImage(width, height, defaultImageType);
	}

	public static BufferedImage makeBufferedImage(int width, int height, int imageType)
	{	return new BufferedImage(width, height, imageType);
	}

	public static BufferedImage makeBufferedImage(Image image)
	{	return makeBufferedImage(image, defaultImageType);
	/*
		BufferedImage bufferedImage = makeBufferedImage(image.getWidth(null),image.getHeight(null));
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, null, null);
		return bufferedImage;		
	*/
	}

	public static BufferedImage makeBufferedImage(Image image, int imageType)
	{	if(waitForImage(image) == false) return null;
		BufferedImage bufferedImage = new BufferedImage(
				image.getWidth(null), image.getHeight(null), imageType);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, null, null);
		return bufferedImage;
	}

	// This method reduces a image if its size/side is greater than print size/side
	public static Image resizeImage(Image image, int printWidth, int printHeight) throws ImageLoadException
	{	// adjust image
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		
		int w = imageWidth;
		int h = imageHeight;
		int iw = printWidth;
		int ih = printHeight;
		if(w > iw && h < ih)
		{ h = iw * h / w;
			w = iw;
		} 
		else if(w < iw && h > ih)
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
/*
		} else // w < iw && h < ih
		{	w = ih * w / h;
			h = ih;
			// if over adjust
			if(w > iw) 
			{ h = iw * h / w;
				w = iw;
			}
*/
		}
			w = Math.max(w,1);
			h = Math.max(h,1);
			Image adjustImage = image.getScaledInstance(w,h,Image.SCALE_DEFAULT);
			if(!ImageUtilities.waitForImage(adjustImage))
			{	throw new ImageLoadException("fail to load image", image);
			}
		return adjustImage;
	}

	// this method reduces image size to (printWidth, printHeight)
	// and fill the other rectangle with background
	public static BufferedImage resizeImage(Image image, int printWidth, int printHeight, Color background) 
			throws ImageLoadException
	{	return resizeImage(image, printWidth, printHeight, background, defaultImageType);
	}

	public static BufferedImage resizeImage(Image image, int printWidth, int printHeight, 
							Color background, int imageType) throws ImageLoadException
	{	// adjust image
		Image adjustImage = resizeImage(image, printWidth, printHeight);
			// prepear printImage
			int imageWidth = adjustImage.getWidth(null);
			int imageHeight = adjustImage.getHeight(null);
			int width = (printWidth - imageWidth)/2;
			int height = (printHeight - imageHeight)/2;

			int lt_x = 0;
			int lt_y = 0;
			int rt_x = lt_x + width + imageWidth;
			int rt_y = lt_y;
			int lb_x = lt_x;
			int lb_y = lt_y + height + imageHeight;
			int rb_x = lt_x;
			int rb_y = lb_y;

			BufferedImage printImage = makeBufferedImage(printWidth, printHeight, imageType);
			Graphics2D g2 = printImage.createGraphics();
			if(background != null)
			{	g2.setColor(background);
				g2.fillRect(0,0,printWidth,printHeight);
			}
			if(imageWidth < printWidth && imageHeight < printHeight)
			{	if(background != null) {
					g2.fillRect(lt_x, lt_y, printWidth, height);
					g2.fillRect(lb_x, lb_y, printWidth, height);
					g2.fillRect(lt_x, lt_y+height, width, imageHeight);
					g2.fillRect(rt_x, rt_y+height, width, imageHeight);
				}
				g2.drawImage(adjustImage, lt_x+width, lt_y+height, null);
			} else
			if(imageWidth < printWidth)
			{	if(background != null) {
					g2.fillRect(lt_x, lt_y, printWidth, height);
					g2.fillRect(lb_x, lb_y, printWidth, height);
				}
				g2.drawImage(adjustImage, lt_x+width, lt_y, null);
			} else
			if(imageHeight < printHeight)
			{	if(background != null) {
					g2.fillRect(lt_x, lt_y, width, printHeight);
					g2.fillRect(rt_x, rt_y, width, printHeight);
				}
				g2.drawImage(adjustImage, lt_x, lt_y+height, null);
			} else
			{	g2.drawImage(adjustImage, lt_x, lt_y, null);
			}
			return printImage;			
	}

	public static BufferedImage resizeImageAndFrame(Image image, int printWidth, int printHeight, Color background) 
			throws ImageLoadException
	{	
			return resizeImage(image, printWidth, printHeight, background);
	}

}