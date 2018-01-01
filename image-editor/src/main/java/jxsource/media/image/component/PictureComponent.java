package jxsource.media.image.component;

import java.awt.Image;

public class PictureComponent extends ImageComponent
{	
	public PictureComponent(Image image)
	{	super(image);
		propertyView.addPropertyToView(strX);
		propertyView.addPropertyToView(strY);
		propertyView.addPropertyToView(strWidth);
		propertyView.addPropertyToView(strHeight);
		propertyView.addPropertyToView(strDistortion);
		propertyView.addPropertyToView(strSelectImageX);
		propertyView.addPropertyToView(strSelectImageY);
		propertyView.addPropertyToView(strSelectImageWidth);
		propertyView.addPropertyToView(strSelectImageHeight);

	}

	public static void main(String[] args)
	{	try {
			java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(args[0]);
			if(!jxsource.media.image.ImageUtilities.waitForImage(image))
				throw new Exception("invalid path "+args[0]);
			javax.swing.JFrame f = new javax.swing.JFrame();
			java.awt.Container c = f.getContentPane();
			c.add(new PictureComponent(image));
			f.pack();
			f.setVisible(true);
		}	catch(Exception e)
		{	e.printStackTrace();
		}
	}

}