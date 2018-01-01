package jxsource.media.image;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.UIManager;

import jxsource.media.image.ImageViewer.FileButton;

public class ImageManager extends ImageViewer {

	MenuItem itemDelete;
	int keepWidth = 950;//1024;
	int keepHeight = 630;//768;

	class ActionDelete extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{	
			if(listPanel.getComponentCount() > 0)
			{
				int i=0;
				while(i<listPanel.getComponentCount())
				{	FileButton fb = (FileButton)listPanel.getComponent(i);
					if(fb.getImageWidth() < keepWidth && fb.getImageHeight() < keepHeight)
					{	
						File f = new File(fb.getPath());
						File dest = new File(f.getPath()+".deleted");
						f.renameTo(dest);
						listPanel.remove(i);
						fb.finalize();
					} else
					{ i++;
					}
				}
				currentIndex = resetCurrentIndex();
				listPanel.revalidate();
				listPanel.repaint();
			}
		}
	}

	public ImageManager() {
		super();
		itemRemoveSelection.removeActionListener(this.getActionRemoveSelection());
		itemKeepSelection.removeActionListener(this.getActionKeepSelection());
		menuEdit.remove(itemRemoveSelection);
		menuEdit.remove(itemKeepSelection);
		itemDelete = new MenuItem("Delete");
		itemDelete.addActionListener(new ActionDelete());
		menuEdit.add(itemDelete);
	}
	public static void main(String[] args)
	{	try {
			String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
	    UIManager.setLookAndFeel(systemLookAndFeelClassName);
		} catch (Exception exc) {}
		ImageManager iv = new ImageManager();
		for(int i=0; i<args.length; i++)
		{ File f = new File(args[i]);
			if(f.exists())
			{	if(f.isDirectory())
					iv.open(f, ImageViewer.OpenDirectory);
				else
					iv.open(f, 2);
			} else
			{	System.out.println(f.getPath()+" does not exist.");
			}
		}
		if(args.length > 0)
		{	while(iv.getCurrentIndex() == -1)
			{	try{Thread.currentThread().sleep(100);} catch(Exception e) {}
			}
			iv.displayIndex(iv.getCurrentIndex());
		}
	}

}
