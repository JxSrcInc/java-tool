package jxsource.util.swing;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.awt.image.BufferedImage;
import jxsource.util.buffer.bytebuffer.ByteArray;
import javax.swing.ImageIcon;
import java.io.InputStream;

public class ResourceUtilities
{	public static ResourceBundle getResourceBundle(String resourceClassName) throws MissingResourceException
	{	ResourceBundle resources = null;
		try {
			resources = ResourceBundle.getBundle(resourceClassName,Locale.getDefault());
		} catch(MissingResourceException e) {e.printStackTrace();}
		if(resources == null)
		{	resources = ResourceBundle.getBundle(resourceClassName,new Locale("en","US"));
		}
		return resources;
	}

	public static ImageIcon getImageIcon(String path)
	{	
		try
		{	InputStream in = ResourceUtilities.class.getClassLoader().getResourceAsStream(path);
			ByteArray ba = new ByteArray();
			int n = 0;
			byte[] buff = new byte[4096];
			while((n = in.read(buff)) != -1)
			{	//TODO:
				//ba.add(buff,0,n);
				ba.append(buff,0,n);
			}
			in.close();
			//TODO:
//			return new ImageIcon(ba.getBytes());
			return new ImageIcon(ba.getArray());
		} catch(Exception e) {}

		return new ImageIcon(new java.awt.image.BufferedImage(10,10,java.awt.image.BufferedImage.TYPE_INT_RGB));
	}

}