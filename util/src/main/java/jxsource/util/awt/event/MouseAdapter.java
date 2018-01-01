package jxsource.util.awt.event;

import java.awt.event.MouseEvent;

public class MouseAdapter extends java.awt.event.MouseAdapter
{	MouseEvent mouseEvent;
	Thread waitingThread;
	int waitingTime;
	Object waitSync = new Object();

	public MouseAdapter(int waitingTime)
	{ this.waitingTime = waitingTime;
	}

	public void mouseClicked(MouseEvent event)
	{	synchronized(waitSync)
		{	mouseEvent = new MouseEvent(event.getComponent(),
																	event.getID(),
																	event.getWhen(),
																	event.getModifiers(),
																	event.getX(),
																	event.getY(),
																	event.getClickCount(),
																	event.isPopupTrigger());
			if(waitingThread == null || !waitingThread.isAlive())
			{	waitingThread = new WaitingThread();
				waitingThread.start();
			}
		}
	}

	// need overwrite in sub class
	public void _mouseClicked(MouseEvent event)
	{
	}

	class WaitingThread extends Thread
	{	public void run()
		{	//System.out.println("waiting thread");
			try
			{ Thread.currentThread().sleep(waitingTime);
			} catch(Exception e) {}
			synchronized(waitSync)
			{	_mouseClicked(mouseEvent);
				mouseEvent = null;
			}
		}
	}
}