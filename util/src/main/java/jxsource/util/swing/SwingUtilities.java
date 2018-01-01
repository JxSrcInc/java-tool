package jxsource.util.swing;

import java.awt.*;

public class SwingUtilities
{
	public static Component getRootContainer(Component c)
	{	Component parent = c.getParent();
		if(parent == null || (parent instanceof Frame) || (parent instanceof Dialog))
			return parent;
		else
			return SwingUtilities.getRootContainer(parent);
	}
}