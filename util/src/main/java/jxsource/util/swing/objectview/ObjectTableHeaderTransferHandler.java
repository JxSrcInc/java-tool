package jxsource.util.swing.objectview;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import jxsource.apps.transfer.objectxml.*;
import java.util.*;
import javax.swing.table.JTableHeader;

public class ObjectTableHeaderTransferHandler extends ObjectTableTransferHandler {

    protected String exportString(JComponent c) {
			return null;
    }

    protected boolean importString(JComponent c, String str) {
		return super.importString(((JTableHeader)c).getTable(),str);
    }
    protected void cleanup(JComponent c, boolean remove) {
    }
}
