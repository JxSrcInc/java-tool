package jxsource.util.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class ScreenSizeWindow extends Window
{	Frame parent;
	ScreenSizeWindowControll controll;

	public ScreenSizeWindow(Frame frame)
	{	super(frame);
		parent = frame;
		setLayout(null);
		setBackground(Color.black);
		Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds (0, 0, dimScreen.width, dimScreen.height );
		setSize(dimScreen);
	}

	public void setControll(ScreenSizeWindowControll controll)
	{	this.controll = controll;
	}
	
	public ScreenSizeWindowControll getControll()
	{	return controll;
	}

	public void display()
	{
		if(controll != null)
			controll.installScreenSizeWindow();
    Toolkit.getDefaultToolkit().sync();
 		validate();
 		setVisible ( true );
//		requestFocus();
	}

	public void dispose() {
		if(controll != null)
			controll.uninstallScreenSizeWindow();
    Toolkit.getDefaultToolkit().sync();
    setVisible ( false );
	}

	public static void main(String[] args)
	{	JFrame frame = new JFrame();
		frame.setSize(400,300);
		frame.addWindowListener(new WindowAdapter()
		{	public void windowClosing(WindowEvent we)
			{	System.exit(0);
			}
		});
		frame.addKeyListener(new KeyAdapter()
		{ public void keyPressed(KeyEvent e) 
			{	System.out.println("--> "+e.getSource().getClass().getName());
			}
		});
		frame.addFocusListener(new FocusAdapter()
		{	public void focusGained(FocusEvent fe)
			{	System.out.println("focusGained: ");
			}
			public void focusLost(FocusEvent fe)
			{	System.out.println("focusLost: ");
			}
		});
		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		JMenu menu = new JMenu("View");
		bar.add(menu);
		JMenuItem itemScreenSizeWindow = new JMenuItem("Full Window");
		itemScreenSizeWindow.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent ae)
			{ Runnable r = new Runnable()
				{	public void run()
					{	try{Thread.currentThread().sleep(300);} catch(Exception e) {}
						ssw.display();
					}
				};
				new Thread(r).start();
			}
		});
		menu.add(itemScreenSizeWindow);
		ssw = new ScreenSizeWindow(frame);
		frame.show();
	}
static ScreenSizeWindow ssw;
}