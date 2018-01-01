package jxsource.util.swing;

import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Cursor;

public class EventBlockGlassPane extends JComponent 
{ public EventBlockGlassPane()
	{	CBListener listener = new CBListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	public void setVisible(boolean b)
	{	if(b)
		{	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else
		{	setCursor(Cursor.getDefaultCursor());
		}
		super.setVisible(b);
	}

	public boolean getVisible()
	{	return super.isVisible();
	}

	class CBListener extends MouseInputAdapter 
	{
    public CBListener() 
		{
    }

    public void mouseMoved(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    /*
     * We must forward at least the mouse drags that started
     * with mouse presses over the check box.  Otherwise,
     * when the user presses the check box then drags off,
     * the check box isn't disarmed -- it keeps its dark
     * gray background or whatever its L&F uses to indicate
     * that the button is currently being pressed.
     */
    public void mouseDragged(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseClicked(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseEntered(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseExited(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mousePressed(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseReleased(MouseEvent e) {
        redispatchMouseEvent(e, true);
    }

    private void redispatchMouseEvent(MouseEvent e,
                                      boolean repaint) 
		{
		}
	}
}
