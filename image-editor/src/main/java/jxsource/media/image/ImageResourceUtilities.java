package jxsource.media.image;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

import jxsource.util.buffer.ByteUtil;

import java.io.*;
import java.net.URL;

public class ImageResourceUtilities
{	
  	public static InputStream getSrcInputStream(String dir, String path) throws Exception
  	{
  		if(System.getProperty("user.dir").equals(dir))
  			return new FileInputStream(new File(System.getProperty("user.dir"),path));
  		URL url = new URL(dir+"/"+path);
  		return url.openStream();
  	}

	public static ResourceBundle getResourceBundle(InputStream in) throws Exception
	{	
		return new PropertyResourceBundle(in);
	}

	public static ResourceBundle getResourceBundle(String dir, String path) throws Exception
	{	
		return getResourceBundle(getSrcInputStream(dir, path));
	}

	public static ImageIcon getImageIcon(InputStream in)
	{	
		try
		{	
			byte[] imageBytes = new byte[0];
			int n = 0;
			byte[] buff = new byte[4096];
			while((n = in.read(buff)) != -1)
			{	
				imageBytes = ByteUtil.append(imageBytes,buff,0,n);
			}
			in.close();
			return new ImageIcon(imageBytes);
		} catch(Exception e) 
		{
			e.printStackTrace();
		}

		return new ImageIcon(new java.awt.image.BufferedImage(10,10,java.awt.image.BufferedImage.TYPE_INT_RGB));
	}

	public static ImageIcon getImageIcon(String dir, String path)
	{	
		try
		{
			return getImageIcon(getSrcInputStream(dir, path));
		} catch(Exception ioe)
		{
			ioe.printStackTrace();
			return null;
		}
	}

}