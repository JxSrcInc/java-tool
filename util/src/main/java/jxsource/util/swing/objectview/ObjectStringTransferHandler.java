package jxsource.util.swing.objectview;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
//import jxsource.util.awt.dnd.StringTransferHandler;
import jxsource.apps.transfer.objectxml.*;
import java.util.*;

public abstract class ObjectStringTransferHandler extends TransferHandler {
    DataFlavor localObjectStringFlavor, serialObjectStringFlavor;
	int sourceActions = COPY_OR_MOVE;

	protected abstract String exportString(JComponent c);
	protected abstract boolean importString(JComponent c, String str);
    protected abstract void cleanup(JComponent c, boolean remove);

	public void setDataFlavor(Class c, String name) 
	{
		String localObjectStringType = DataFlavor.javaJVMLocalObjectMimeType +
                                ";class="+c.getClass().getName();
        try {
            localObjectStringFlavor = new DataFlavor(localObjectStringType);
        } catch (ClassNotFoundException e) {
            System.out.println(
             "ObjectStringTransferHandler: unable to create data flavor");
        }
        serialObjectStringFlavor = new DataFlavor(c,name);
	}

	public void setSourceActions(int sourceAction)
	{	
		this.sourceActions = sourceAction;
	}

    public boolean importData(JComponent c, Transferable t) 
    {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                String str = (String)t.getTransferData(serialObjectStringFlavor);
                return importString(c, str);
//                return true;
            } catch (UnsupportedFlavorException ufe) {
            } catch (IOException ioe) { ioe.printStackTrace();
            }
        }
        return false;
    }

    protected void exportDone(JComponent c, Transferable data, int action) {
        cleanup(c, action == MOVE);
    }

    private boolean hasSerialObjectStringFlavor(DataFlavor[] flavors) 
    {
        if (serialObjectStringFlavor == null) {
            return false;
        }

        for (int i = 0; i < flavors.length; i++) {
					Class localClass = serialObjectStringFlavor.getRepresentationClass();
					Class transClass = flavors[i].getRepresentationClass();
					while(transClass != null && !localClass.getName().equals(transClass.getName()))
					{ 
						transClass = transClass.getSuperclass();
					}
            if (transClass != null) {
                return true;
            }
        }
        return false;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) 
    {
// remove validation on hasLocalObjectStringFlaver to insure no other class can import
// Note: localObjectStringFlavor itself cannot be removed! -- I don't know why. But removing it makes DND no more working
//        if (hasLocalObjectStringFlavor(flavors))  { return true; }
        if (hasSerialObjectStringFlavor(flavors)) { return true; }
        return false;
    }

    protected Transferable createTransferable(JComponent c) 
    {
		Transferable f = new ObjectStringTransferable(exportString(c));
        return f;
    }

    public int getSourceActions(JComponent c) {
        return sourceActions;
    }

    public class ObjectStringTransferable implements Transferable {
        String data;

        public ObjectStringTransferable(String alist) {
            data = alist;
        }

        public Object getTransferData(DataFlavor flavor)
                                 throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return data;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { localObjectStringFlavor,
                                      serialObjectStringFlavor };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
					// this is tested in export handler, so the parent and child relation is reversed as above
					Class _class = flavor.getRepresentationClass();
					Class c = serialObjectStringFlavor.getRepresentationClass();
					while(c != null && !_class.getName().equals(c.getName()))
					{ c = c.getSuperclass();
					}
					if(c != null)
						return true;
/*
            if (serialObjectStringFlavor.equals(flavor)) {
                return true;
            }
*/
            if (localObjectStringFlavor.equals(flavor)) {
                return true;
            }
            return false;

        }
    }
}
