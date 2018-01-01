package jxsource.media.image;

import javax.swing.*;
import jxsource.util.swing.DownLayout;
import java.util.Vector;
import java.awt.*;

public class EditableImageListPanel extends JPanel
{	
	public EditableImageListPanel()
	{	setLayout(new DownLayout());
	}

	public void clear()
	{	removeAll();
		revalidate();
		repaint();
	}
			
}