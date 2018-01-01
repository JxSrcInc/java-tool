package jxsource.media.image;

import java.awt.Image;

public class ImageLoadException extends Exception
{ Image image;

	public ImageLoadException()
	{	super();
	}

	public ImageLoadException(String msg)
	{	super(msg);
	}

	public ImageLoadException(Image image)
	{	this.image = image;
	}

	public ImageLoadException(String msg, Image image)
	{	super(msg);
		this.image = image;
	}

	public Image getImage()
	{	return image;
	}
}