package jxsource.media.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import jxsource.media.image.component.ImageComponent;
import jxsource.media.image.component.PictureComponent;
import jxsource.media.image.component.RectangleComponent;
import jxsource.media.image.component.StringComponent;
import jxsource.util.io.FileSearchUtil;
import jxsource.util.io.filter.ExtensionFileChooserFilter;
import jxsource.util.swing.CellListPanel;;

public class ImageEditor extends JFrame
{	JPanel contentPane;
	ActionSearch actionSearch = new ActionSearch();
	ActionPauseAndResume actionPauseAndResume = new ActionPauseAndResume();
	ActionOpenDirectory actionOpenDirectory = new ActionOpenDirectory();
	ActionOpenFile actionOpenFile = new ActionOpenFile();
	ActionOpenList actionOpenList = new ActionOpenList();
	ActionExit actionExit = new ActionExit();
	ActionRemoveComponent actionRemoveComponent = new ActionRemoveComponent();
	ActionSaveProcessed actionSaveProcessed = new ActionSaveProcessed();
	ActionRemoveSelection actionRemoveSelection = new ActionRemoveSelection();
	ActionKeepSelection actionKeepSelection = new ActionKeepSelection();
	ActionResizeIcon actionResizeIcon = new ActionResizeIcon();
	ActionStringComponent actionStringComponent = new ActionStringComponent();
	ActionRectangleComponent actionRectangleComponent = new ActionRectangleComponent();
	ActionBackground actionBackground = new ActionBackground();
	ActionView actionView = new ActionView();
	JFileChooser chooser;
	JFileChooser savechooser;
	ImageEditor imageEditor;
	CellListPanel listPanel;
	EditableImageListPanel editableImageListPanel;
	ImageEditPanel imagePanel;
	JLabel message, memory, source;
	jxsource.util.swing.GridLayout listLayout;
	JSplitPane splitPane;
	JPanel plainPanel;
	volatile boolean loading = false;
	JScrollPane imageScroller;
	Image srcImage;
	Image processedImage;
	String srcImageSource;
	Image image;
	Vector editImages; // images in editPanel and editableListPanel

    FilePreviewer previewer;
    FilePreviewer savepreviewer;
    
	int minIconSize = 40;
	int maxIconSize = 120;
	int defaultIconSize = minIconSize;
	int maxIconFileSize = 10000;
	int maxImageNumber = 5000;

	int splitPaneDividerLocation = 205;

	final int Search = 0;
	final int OpenDirectory = 1;
	final int OpenFile = 2;

	Toolkit toolkit = Toolkit.getDefaultToolkit();
  private static final Insets shrinkwrap = new Insets(0,0,0,0);
	private int iconWidth = 16;
	private int iconHeight = 16;

	private int screenWidth;
	private int screenHeight;

	String strImageEditor = "Image Editor";
	String strSource = "Source";
	String strFile = "File";
	String strOpen = "Open";
	String strOpenList = "Open List";
	String strSearch = "Search";
	String strOpenDirectory = "Open Directory";
	String strOpenFile = "Open File";
	String strRectangle = "Rectangle";
	String strString = "String";
//	String strSaveSourceImage = "Source Image";
	String strSaveProcessedImage = "Save";
//	String strSaveSelectedImage = "Save Selected Image";
//	String strView = "View";
//	String strDefaultView = "Default Size";
//	String strFitWindow = "Fit Window";
//	String strZoom = "Zoom ...";
//	String strAnimation = "Animation";
//	String strStart = "Start";
//	String strStop = "Stop";
//	String strStartAnimation = strStart;
//	String strStopAnimation = strStop;
//	String strNextImage = "Next Image";
//	String strPreviousImage = "Previous Image";
//	String strFirstImage = "First Image";
//	String strScreenSizeWindow = "Screen Size Window";
//	String strRestore = "Restore";
	String strResizeSelectionIcon = "Resize Selection Icon";
	String strEdit = "Edit";
	String strRemoveSelection = "Remove Selected Images";
	String strKeepSelection = "Keep Selected Images";
	String strRemoveComponent = "Remove All Image Components";
//	String strOption = "Option";
//	String strMaxIconFileSize = "Max Cacheable File Size ";
	String strReady = "ready";
	String strFailToLoadImage = "fail to load image";
	String strPause = "Pause ......";
	String strStopByUser = "Stopped by user's request.";
	String strStartSearch = "Start search image files in a directory and its sub-directories";
	String strStopSearch = "Stop search";
	String strExit = "Exit";
	String strDefault = "Default";
	String strTransparency = "Transparency";
	String strColor = "Color";
	String strBackground= "Background";
	String strTool = "Tool";

	String searchToolTipText;
 	String searchAccessibleName;
	String pauseToolTipText;
	String pauseAccessibleName;
	String openFileToolTipText;
	String openFileAccessibleName;
	String saveFileToolTipText;
	String saveFileAccessibleName;
	String exitToolTipText;
	String exitAccessibleName;
	String clearToolTipText;
	String clearAccessibleName;
	String windowSizeToolTipText;
	String windowSizeAccessibleName;
	String animationToolTipText;
	String animationAccessibleName;
	String animationStopToolTipText;
	String animationStopAccessibleName;
	String prevIamgeToolTipText;
	String prevIamgeAccessibleName;
	String nextIamgeToolTipText;
	String nextIamgeAccessibleName;

	Icon searchIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/find.gif");
	ImageIcon	openFileIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/file.gif");
	ImageIcon saveFileIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/save.gif");
	ImageIcon exitIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/exit.gif");
	Icon clearIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/delete.jpg");
	ImageIcon windowSizeIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/WindowSize.gif");
	Icon animationIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/video.gif");
	Icon animationStopIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/video-disabled.gif");
	ImageIcon	prevImageIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/wht_prev.gif");
	ImageIcon nextImageIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/wht_next.gif");
	Icon pauseIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/pause.gif");
	Icon resumeIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/resume.gif");
	Icon stopIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/stop.gif");
	Icon openListIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/FileOpen.gif");
	ImageIcon textIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/text.jpg");
	ImageIcon rectangleIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/rectangle.jpg");
	ImageIcon colorIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/color.jpg");

	final int stop = 0;
	final int pause = 1;
	final int running = 2;
	volatile int loadingStatus = stop;
	Object sync = new Object();
	JButton pauseButton;
	JButton searchButton;
	JMenuItem itemStartSearch;
	JMenuItem itemStopSearch;
	JTabbedPane tabPane;
	final int sourceTabIndex = 0;
	final int editTabIndex = 1;

	public ImageEditor()
	{	setSize(800,500);
		imageEditor = this;
		setTitle(strImageEditor);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = screenSize.width;
		screenHeight = screenSize.height;

		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());
		chooser = new JFileChooser();
		previewer = new FilePreviewer(chooser);
	    chooser.setAccessory(previewer);
	    chooser.setMultiSelectionEnabled(true);
		savechooser = new JFileChooser();
		savepreviewer = new FilePreviewer(savechooser);
	    savechooser.setAccessory(savepreviewer);

		JToolBar toolbar = new JToolBar();
		JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toolbarPanel.add(toolbar);
		contentPane.add(toolbarPanel, BorderLayout.NORTH);

		searchButton = new JButton(getActionSearch());
		searchButton.setText(null);
		searchButton.setIcon(searchIcon = resizeToolBarIcon(searchIcon));
		stopIcon = resizeToolBarIcon(stopIcon);
   	searchButton.setToolTipText(searchToolTipText);
   	searchButton.getAccessibleContext().setAccessibleName(searchAccessibleName);
		searchButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		searchButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		searchButton.setMargin(shrinkwrap);
		toolbar.add(searchButton);

		pauseButton = new JButton(getActionPauseAndResume());
		pauseButton.setVisible(false);
		pauseButton.setText(null);
		pauseButton.setIcon(pauseIcon = resizeToolBarIcon(pauseIcon));
		resumeIcon = resizeToolBarIcon(resumeIcon);
   	pauseButton.setToolTipText(pauseToolTipText);
   	pauseButton.getAccessibleContext().setAccessibleName(pauseAccessibleName);
		pauseButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		pauseButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		pauseButton.setMargin(shrinkwrap);
		toolbar.add(pauseButton);

		JButton openFileButton = new JButton();
		openFileButton.addActionListener(getActionOpenFile());
		openFileButton.setText(null);
		openFileButton.setIcon(resizeToolBarIcon(openFileIcon));
   	openFileButton.setToolTipText(openFileToolTipText);
   	openFileButton.getAccessibleContext().setAccessibleName(openFileAccessibleName);
		openFileButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		openFileButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		openFileButton.setMargin(shrinkwrap);
		toolbar.add(openFileButton);

		JButton openListButton = new JButton();
		openListButton.addActionListener(getActionOpenList());
		openListButton.setText(null);
		openListButton.setIcon(resizeToolBarIcon(openListIcon));
//   	openListButton.setToolTipText(openListToolTipText);
//   	openListButton.getAccessibleContext().setAccessibleName(openListAccessibleName);
		openListButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		openListButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		openListButton.setMargin(shrinkwrap);
		toolbar.add(openListButton);

		JButton saveFileButton = new JButton();
		saveFileButton.addActionListener(getActionSaveProcessed());
		saveFileButton.setText(null);
		saveFileButton.setIcon(resizeToolBarIcon(saveFileIcon));
//   	saveFileButton.setToolTipText(saveFileToolTipText);
//   	saveFileButton.getAccessibleContext().setAccessibleName(saveFileAccessibleName);
		saveFileButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		saveFileButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		saveFileButton.setMargin(shrinkwrap);
		toolbar.add(saveFileButton);

		JButton exitButton = new JButton(getActionExit());
//		exitButton.setText(null);
		exitButton.setIcon(resizeToolBarIcon(exitIcon));
   	exitButton.setToolTipText(exitToolTipText);
   	exitButton.getAccessibleContext().setAccessibleName(exitAccessibleName);
		exitButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		exitButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		exitButton.setMargin(shrinkwrap);
		toolbar.add(exitButton);

		JButton clearButton = new JButton(getActionRemoveComponent());
		clearButton.setIcon(resizeToolBarIcon(clearIcon));
   	clearButton.setToolTipText(clearToolTipText);
   	clearButton.getAccessibleContext().setAccessibleName(clearAccessibleName);
		clearButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		clearButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		clearButton.setMargin(shrinkwrap);
		toolbar.add(clearButton);

		toolbar.add(Box.createHorizontalStrut(5));

		JButton stringComponentButton = new JButton(getActionStringComponent());
		stringComponentButton.setMargin(shrinkwrap);
		stringComponentButton.setIcon(resizeToolBarIcon(textIcon));
		toolbar.add(stringComponentButton);
		JButton rectComponentButton = new JButton(getActionRectangleComponent());
		rectComponentButton.setMargin(shrinkwrap);
		rectComponentButton.setIcon(resizeToolBarIcon(rectangleIcon));
		toolbar.add(rectComponentButton);
		JButton backgroundButton = new JButton(getActionBackground());
		backgroundButton.setText(strBackground);
		backgroundButton.setMargin(new Insets(0,0,0,0));
		backgroundButton.setIcon(resizeToolBarIcon(colorIcon));
//  	clearButton.setToolTipText(clearToolTipText);
//   	clearButton.getAccessibleContext().setAccessibleName(clearAccessibleName);
//		clearButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
//		clearButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		backgroundButton.setMargin(shrinkwrap);
		toolbar.add(backgroundButton);


		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuFile = new JMenu(strFile);
		menuBar.add(menuFile);
		JMenu menuOpen = new JMenu(strOpen);
		menuFile.add(menuOpen);
		JMenuItem itemOpenDirectory = new JMenuItem(strOpenDirectory);
		itemOpenDirectory.addActionListener(getActionOpenDirectory());
		menuOpen.add(itemOpenDirectory);
		JMenuItem itemOpenFile = new JMenuItem(strOpenFile);
		itemOpenFile.addActionListener(getActionOpenFile());
		menuOpen.add(itemOpenFile);
		JMenuItem itemSaveProcessed = new JMenuItem(strSaveProcessedImage);
		menuFile.add(itemSaveProcessed);
		itemSaveProcessed.addActionListener(getActionSaveProcessed());
		JMenuItem itemExit = new JMenuItem(strExit);
		menuFile.add(itemExit);
		itemExit.addActionListener(getActionExit());

		JMenu menuSearch = new JMenu(strSearch);
		menuBar.add(menuSearch);
		itemStartSearch = new JMenuItem(strStartSearch);
		itemStartSearch.addActionListener(getActionSearch());
		menuSearch.add(itemStartSearch);
		itemStopSearch = new JMenuItem(strStopSearch);
		itemStopSearch.addActionListener(getActionSearch());
		itemStopSearch.setEnabled(false);
		menuSearch.add(itemStopSearch);

		JMenu menuTool = new JMenu(strTool);
		menuBar.add(menuTool);

		JMenuItem itemStringComponent = new JMenuItem(strString);
		itemStringComponent.addActionListener(getActionStringComponent());
		menuTool.add(itemStringComponent);
		JMenuItem itemRectangleComponent = new JMenuItem(strRectangle);
		itemRectangleComponent.addActionListener(getActionRectangleComponent());
		menuTool.add(itemRectangleComponent);
		JMenuItem itemBackground = new JMenuItem(strBackground);
		itemBackground.addActionListener(getActionBackground());
		menuTool.add(itemBackground);
		JMenuItem itemView = new JMenuItem("View");
		itemView.addActionListener(getActionView());
		menuTool.add(itemView);

		JMenu menuEdit = new JMenu(strEdit);
		menuBar.add(menuEdit);
		JMenuItem itemRemoveSelection = new JMenuItem(getActionRemoveSelection());
		itemRemoveSelection.setText(strRemoveSelection);
		menuEdit.add(itemRemoveSelection);
		JMenuItem itemKeepSelection = new JMenuItem(getActionKeepSelection());
		itemKeepSelection.setText(strKeepSelection);
		menuEdit.add(itemKeepSelection);
		menuEdit.addSeparator();
		JMenuItem itemRemoveComponent = new JMenuItem(getActionRemoveComponent());
		getActionRemoveComponent().setEnabled(false);
		itemRemoveComponent.setText(strRemoveComponent);
		menuEdit.add(itemRemoveComponent);

/*
		menuView.addSeparator();

		Menu menuAnimation = new Menu(strAnimation);
		menuView.add(menuAnimation);
		itemAnimation = new MenuItem(strStartAnimation, new MenuShortcut(KeyEvent.VK_A));
		itemAnimation.addActionListener(getActionAnimation());
		menuAnimation.add(itemAnimation);
		itemStopAnimation = new MenuItem(strStopAnimation, new MenuShortcut(KeyEvent.VK_S));
		itemStopAnimation.addActionListener(getActionAnimation());
		itemStopAnimation.setEnabled(false);
		menuAnimation.add(itemStopAnimation);
		MenuItem itemNextImage = new MenuItem(strNextImage, new MenuShortcut(KeyEvent.VK_N));
		itemNextImage.addActionListener(getActionNextImage());
		menuView.add(itemNextImage);
		MenuItem itemPrevImage = new MenuItem(strPreviousImage, new MenuShortcut(KeyEvent.VK_P));
		itemPrevImage.addActionListener(getActionPrevImage());
		menuView.add(itemPrevImage);
		MenuItem itemFirstImage = new MenuItem(strFirstImage, new MenuShortcut(KeyEvent.VK_F));
		itemFirstImage.addActionListener(getActionFirstImage());
		menuView.add(itemFirstImage);
		menuView.addSeparator();
		MenuItem itemScreenSizeWindow = new MenuItem(strScreenSizeWindow, new MenuShortcut(KeyEvent.VK_W));
		menuView.add(itemScreenSizeWindow);
		itemScreenSizeWindow.addActionListener(getActionScreenSizeWindow());
		MenuItem itemRestore = new MenuItem(strRestore, new MenuShortcut(KeyEvent.VK_R));
		menuView.add(itemRestore);
		itemRestore.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent ae)
			{	if(fullScreenDisplay)
					screenSizeWindow.dispose();
			}
		});
		menuView.addSeparator();
		MenuItem itemResizeIcon = new MenuItem(strResizeSelectionIcon, new MenuShortcut(KeyEvent.VK_I));
		menuView.add(itemResizeIcon);
		itemResizeIcon.addActionListener(getActionResizeIcon());

		Menu menuEdit = new Menu(strEdit);
		menuBar.add(menuEdit);
		MenuItem itemRemoveSelection = new MenuItem(strRemoveSelection);
		itemRemoveSelection.addActionListener(getActionRemoveSelection());
		menuEdit.add(itemRemoveSelection);
		MenuItem itemKeepSelection = new MenuItem(strKeepSelection);
		itemKeepSelection.addActionListener(getActionKeepSelection());
		menuEdit.add(itemKeepSelection);

		Menu menuOption = new Menu(strOption);
		menuBar.add(menuOption);
		MenuItem itemMaxIconFileSize = new MenuItem(strMaxIconFileSize);
		itemMaxIconFileSize.addActionListener(getActionMaxIconFileSize());
		menuOption.add(itemMaxIconFileSize);
*/
		addWindowListener(new WindowAdapter()
		{	public void windowClosing(WindowEvent we)
			{	System.exit(0);
			}
		});
		listPanel = new CellListPanel(defaultIconSize,defaultIconSize,0,5);
		listPanel.setBackground(Color.white);
		JScrollPane listScroller = new JScrollPane(listPanel);
		listPanel.setContainer();

		editImages = new Vector();
		editableImageListPanel = new EditableImageListPanel();
		JScrollPane editScroller = new JScrollPane(editableImageListPanel);

		tabPane = new JTabbedPane();
		tabPane.add(strSource,listScroller);
		tabPane.add(strEdit, editScroller);
		tabPane.addChangeListener(new ChangeListener()
		{	public void stateChanged(ChangeEvent ce)
			{ switch(tabPane.getSelectedIndex())
				{	case sourceTabIndex:
						getActionRemoveSelection().setEnabled(true);
						getActionKeepSelection().setEnabled(true);
						getActionRemoveComponent().setEnabled(false);
						break;
					case editTabIndex:
						getActionRemoveSelection().setEnabled(false);
						getActionKeepSelection().setEnabled(false);
						getActionRemoveComponent().setEnabled(true);
						break;
				}
			}
		});

		imagePanel = new ImageEditPanel(editImages);
		plainPanel = new JPanel(new BorderLayout());
		plainPanel.add(new JScrollPane(imagePanel),BorderLayout.CENTER);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			tabPane,//listScroller,
			plainPanel);
		splitPane.setDividerSize(2);
		splitPane.setDividerLocation(splitPaneDividerLocation);

		// revalidate listPanel when Divider Location changed
		splitPane.addPropertyChangeListener(new PropertyChangeListener()
		{	public void propertyChange(PropertyChangeEvent event)
			{ if(event.getPropertyName().equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY))
				{	//System.out.println(event);
					if(!loading) {
						Runnable r = new Runnable()
						{	public void run()
							{	listPanel.setSize(listPanel.getSize());
								listPanel.revalidate();
							}
						};
						SwingUtilities.invokeLater(r);
					}
				}
			}
		});

		contentPane.add(splitPane,BorderLayout.CENTER);
		JPanel msgPanel = new JPanel(new GridLayout(1,2));
		msgPanel.add(message = new JLabel(strReady));
		msgPanel.add(source = new JLabel());
		contentPane.add(msgPanel,BorderLayout.SOUTH);

		validate();
		setVisible(true);
	}

	private Icon resizeToolBarIcon(Icon icon)
	{	if(icon instanceof ImageIcon)
		{	Image image = ((ImageIcon)icon).getImage();
			try
			{	image = ImageUtilities.resizeImageAndFrame(image,iconWidth,iconHeight,new JButton().getBackground());
			} catch(Exception e) {}
			((ImageIcon)icon).setImage(image);
		}
		return icon;
	}

	public Image loadImage(String path) throws Exception
	{	// to use jxsource
		srcImageSource = path; //RandomAccessFileManager.getRandomAccessFileManager().create(path,"r",false);

		Image image = toolkit.createImage(srcImageSource);
		if(!ImageUtilities.waitForImage(image))
			throw new IOException(strFailToLoadImage);
//		mem("-- load image "+srcImageSource);
		return image;
	}
/*
static int count = 0;
	private String mem(String info)
	{	Runtime runtime = Runtime.getRuntime();
		long total = runtime.totalMemory()/1000L;
		long free = runtime.freeMemory()/1000L;
		String mem = "memory: totle="+
											Long.toString(total)+"k ,free="+
											Long.toString(free)+"k";
		System.out.println( Integer.toString(count++)+" - "+mem+". "+info);
		if(free*10L < total)
			return mem;
		else
			return null;
	}
*/

	public ActionSearch getActionSearch()
	{	return actionSearch;
	}

	class ActionSearch extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
			if(searchButton.getIcon() == searchIcon)
			{	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				int retval = chooser.showOpenDialog(imageEditor);
				if(retval == JFileChooser.APPROVE_OPTION)
				{ File theFile = chooser.getSelectedFile();
	    		if(theFile != null)
					{	pauseButton.setVisible(true);
						pauseButton.setIcon(pauseIcon);
						searchButton.setIcon(stopIcon);
						itemStartSearch.setEnabled(false);
						itemStopSearch.setEnabled(true);
						loadingStatus = running;
						open(theFile, Search);
		    	}
				}
			} else // stop
			{	loadingStatus = stop;
				synchronized(sync)
				{	sync.notifyAll();
				}
				searchButton.setIcon(searchIcon);
				pauseButton.setVisible(false);
				itemStartSearch.setEnabled(true);
				itemStopSearch.setEnabled(false);
			}
		}
	}

	public ActionPauseAndResume getActionPauseAndResume()
	{	return actionPauseAndResume;
	}

	class ActionPauseAndResume extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
			JButton pauseButton = (JButton)ae.getSource();
			if(pauseButton.getIcon() == pauseIcon)
			{	loadingStatus = pause;
				pauseButton.setIcon(resumeIcon);
			}	else
			{	synchronized(sync)
				{	loadingStatus = running;
					sync.notifyAll();
				}
				pauseButton.setIcon(pauseIcon);
			}
		}
	}

	public ActionBackground getActionBackground()
	{	return actionBackground;
	}

	class ActionBackground extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			JPanel pane = new JPanel(new BorderLayout());
			JList list = new JList(new String[] {
				ImageEditPanel.DEFAULT_BACKGROUND,
				ImageEditPanel.COLOR_BACKGROUND,
				ImageEditPanel.TRANSPARENT_BACKGROUND});
			pane.add(BorderLayout.CENTER,new JScrollPane(list));
			if(JOptionPane.showConfirmDialog(imageEditor,pane,strBackground,
						JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE) == 0)
			{	Object color = list.getSelectedValue();
				if(color != null)
			    imagePanel.setImageBackground((String) color);
			}
		}
	}

	public ActionView getActionView()
	{	return actionView;
	}

	class ActionView extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			String tempFileName = "c:/temp/temp.jpg";
			try
			{ OutputStream out = new FileOutputStream(tempFileName);
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				BufferedImage editedImage = imagePanel.getImage();//ImageUtilities.makeBufferedImage(imagePanel.getAdjustSizeImage());
				int height = editedImage.getHeight();
				int width = editedImage.getWidth();
				if(System.getProperty("jxsource.apps.imageeditor.printwidthdefault") != null)
				{
					// for desktop print 
					width = getPrintWidth(editedImage.getHeight(), editedImage.getWidth());
				}
				if(width == -1)
				{
					width = editedImage.getWidth();
				}
				BufferedImage outImage = ImageUtilities.resizeImage(editedImage, width, height, imagePanel.getBackground()); 
				encoder.encode(outImage);
				out.flush();
				out.close();
			} catch(Exception ex) {ex.printStackTrace();}
			ImageViewer iv = new ImageViewer();
			iv.open(new File(tempFileName), 2);
			iv.removeWindowListener(iv.getWindowListeners()[0]);
			iv.addWindowListener(new WindowAdapter()
			{	public void windowClosing(WindowEvent we) {
				}
			});

/*			
			JDialog frame = new JDialog(imageEditor, true);
			
			frame.setModal(true);
			frame.setSize(600, 400);
			Container contentPane = frame.getContentPane();
			contentPane.setLayout(new BorderLayout());
//			BufferedImage image = imagePanel.getImage();
			Image image = toolkit.createImage(tempFileName);
			if(!ImageUtilities.waitForImage(image)) {
				throw new RuntimeException("Exception when loading: "+tempFileName);
			}
			ImagePanel viewPanel = new ImagePanel();
			viewPanel.setDisplayType(ImagePanel.DEFAULT);
			viewPanel.setImage(viewPanel.fitWindow(image));
			Dimension d = contentPane.getSize();

			contentPane.add(viewPanel, BorderLayout.CENTER);

			frame.setVisible(true);
*/		}
	}

	public ActionStringComponent getActionStringComponent()
	{	return actionStringComponent;
	}

	class ActionStringComponent extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
//			final ImageComponent imageComponent = ((ComponentButton)ae.getSource()).getImageComponent();
			tabPane.setSelectedIndex(editTabIndex);
			Runnable r = new Runnable() {
				public void run() {
					ImageComponent imageComponent = new StringComponent();
					imagePanel.setImageComponent(imageComponent);
					editableImageListPanel.add(imageComponent);
					editableImageListPanel.setSize(editableImageListPanel.getSize());
					editableImageListPanel.revalidate();
				}
			};
			new Thread(r).start();
		}
	}

	public ActionRectangleComponent getActionRectangleComponent()
	{	return actionRectangleComponent;
	}

	class ActionRectangleComponent extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
//			final ImageComponent imageComponent = ((ComponentButton)ae.getSource()).getImageComponent();
			tabPane.setSelectedIndex(1);
			Runnable r = new Runnable() {
				public void run() {
					ImageComponent imageComponent = new RectangleComponent();
					imagePanel.setImageComponent(imageComponent);
					editableImageListPanel.add(imageComponent);
					editableImageListPanel.setSize(editableImageListPanel.getSize());
					editableImageListPanel.revalidate();
				}
			};
			new Thread(r).start();
		}
	}

	public ActionOpenDirectory getActionOpenDirectory()
	{	return actionOpenDirectory;
	}

	class ActionOpenDirectory extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			int retval = chooser.showOpenDialog(imageEditor);
			if(retval == JFileChooser.APPROVE_OPTION)
			{ File theFile = chooser.getSelectedFile();
	    	if(theFile != null)
				{	open(theFile, OpenDirectory);
	    	}
			}
		}
	}

	public ActionOpenList getActionOpenList()
	{	return actionOpenList;
	}

	class ActionOpenList extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
System.out.println("OpenList");
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			int retval = chooser.showOpenDialog(imageEditor);
			if(retval == JFileChooser.APPROVE_OPTION)
			{ File theFile = chooser.getSelectedFile();
				if(theFile != null) 
				{	
					if(theFile.isDirectory())
						open(theFile,1);
					else if(theFile.isFile())
						open(theFile,2);
				}
			}
		}
	}

	public ActionOpenFile getActionOpenFile()
	{	return actionOpenFile;
	}

	class ActionOpenFile extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
System.out.println("OpenFile");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			int retval = chooser.showOpenDialog(imageEditor);
			if(retval == JFileChooser.APPROVE_OPTION)
			{ File theFile = chooser.getSelectedFile();
	    	if(theFile != null)
			{	
	    		open(theFile, OpenFile);
	    	}
			}
		}
	}

	public ActionExit getActionExit()
	{	return actionExit;
	}

	class ActionExit extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
			System.exit(0);
		}
	}

	public ActionRemoveComponent getActionRemoveComponent()
	{	return actionRemoveComponent;
	}

	class ActionRemoveComponent extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
			imagePanel.clear();
			editableImageListPanel.clear();
		}
	}

	public void open(File theFile, int openType)
	{	try
		{	Thread thread = new Thread(new LoadRunnable(theFile, openType));
				thread.setPriority(Thread.MIN_PRIORITY);
					thread.start();
		} catch(Exception e) {e.printStackTrace();}
	}

	public ActionSaveProcessed getActionSaveProcessed()
	{	return actionSaveProcessed;
	}

	class ActionSaveProcessed extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {

			savechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			savechooser.setFileFilter(new ExtensionFileChooserFilter("jpg", "JPEG file"));
			int retval = savechooser.showSaveDialog(imageEditor);
			if(retval == JFileChooser.APPROVE_OPTION)
			{ File theFile = savechooser.getSelectedFile();
	    	if(theFile != null)
				{	if(theFile.exists())
					{	int ret = JOptionPane.showConfirmDialog(imageEditor,
											"Do you want to overwrite ?\n\n"+theFile.getPath(),
											"warning",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE);
						if(ret != JOptionPane.YES_OPTION) return;
					}
					try
					{ OutputStream out = new FileOutputStream(theFile);
						JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
						BufferedImage editedImage = imagePanel.getImage();//ImageUtilities.makeBufferedImage(imagePanel.getAdjustSizeImage());
						int height = editedImage.getHeight();
						int width = editedImage.getWidth();
						if(System.getProperty("jxsource.apps.imageeditor.printwidthdefault") != null)
						{
							// for desktop print 
							width = getPrintWidth(editedImage.getHeight(), editedImage.getWidth());
						}
						if(width == -1)
						{
							width = editedImage.getWidth();
						}
						BufferedImage outImage = ImageUtilities.resizeImage(editedImage, width, height, imagePanel.getBackground()); 
						encoder.encode(outImage);
						out.flush();
						out.close();
					} catch(Exception e) {e.printStackTrace();}
	    	}
			}

		}
	}
	
	private int getPrintWidth(int h, int w)
	{
		if(h == 768)
			return 1024;
		else if(h == 1440)
			return 1920;
		else if(h == 1200)
			return 1600;
		else if(h > 768)
			return h*1920/1440;
		else
			return -1;

	}

	private int getPrintHeight()
	{
		return 1440;
	}

	public ActionRemoveSelection getActionRemoveSelection()
	{	return actionRemoveSelection;
	}

	class ActionRemoveSelection extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{	if(listPanel.getComponentCount() > 0)
			{
				int i=0;
				while(i<listPanel.getComponentCount())
				{	FileButton fb = (FileButton)listPanel.getComponent(i);
					if(fb.isSelected())
					{	listPanel.remove(i);
						fb.finalize();
					} else
					{ i++;
					}
				}
				listPanel.revalidate();
				listPanel.repaint();
			}
		}
	}

	public ActionKeepSelection getActionKeepSelection()
	{	return actionKeepSelection;
	}

	class ActionKeepSelection extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{	if(listPanel.getComponentCount() > 0)
			{
				int i=0;
				while(i<listPanel.getComponentCount())
				{	FileButton fb = (FileButton)listPanel.getComponent(i);
					if(!fb.isSelected())
					{	listPanel.remove(i);
						fb.finalize();
					} else
					{ i++;
					}
				}
				listPanel.revalidate();
				listPanel.repaint();
			}
		}
	}

	public ActionResizeIcon getActionResizeIcon()
	{	return actionResizeIcon;
	}

	class ActionResizeIcon extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{	JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel("set icon size"),BorderLayout.NORTH);
			JScrollBar scroller = new JScrollBar(JScrollBar.HORIZONTAL,defaultIconSize,0,minIconSize,maxIconSize);
			panel.add(scroller,BorderLayout.CENTER);
			if(JOptionPane.showConfirmDialog(imageEditor, panel) == JOptionPane.YES_OPTION)
			{	//listLayout.setCellWidth(defaultIconSize = scroller.getValue());
				defaultIconSize = scroller.getValue();
				listPanel.setCellSize(defaultIconSize, defaultIconSize);
				for(int i=0; i<listPanel.getComponentCount(); i++)
				{	FileButton fb = (FileButton)listPanel.getComponent(i);
					fb.resizeIcon();
				}
				listPanel.revalidate();
				listPanel.repaint();
			}
		}
	}

	class LoadRunnable implements Runnable
	{	File file;
		int openType;

		public LoadRunnable(File file, int openType)
		{	this.file = file;
			this.openType = openType;
		}

		public void run()
		{	//synchronized(listModel)
			{	message.setText("Loading from "+file.getPath()+" ......");
				loading = true;
System.out.println("openType: "+openType);
				switch(openType)
				{	case Search:
						try
						{	java.io.FileFilter[] fef = new java.io.FileFilter[] {Constant.ImageFileFilter};
							BufferedReader in = new BufferedReader(new InputStreamReader(
									new FileSearchUtil().recursiveSearch(file,fef)));
							String path = null;
							String dir = "";
							loadingStatus = running;
							while((path = in.readLine()) != null && addImageIcon(path))
							{	
								if(loadingStatus == pause)
								{	message.setText(strPause);
									synchronized(sync)
									{	sync.wait();
									}
								}
								if(loadingStatus == stop)
								{ message.setText(strStopByUser);
									break;
								}
							}
							try{in.close();} catch(Exception ex) {}
						} catch(Throwable e)
						{	JOptionPane.showMessageDialog(imageEditor, "Load "+file.getPath()+" error:\n\n"+e.getClass().getName()+":"+e.getMessage());
						}
						pauseButton.setVisible(false);
						searchButton.setIcon(searchIcon);
						itemStartSearch.setEnabled(true);
						itemStopSearch.setEnabled(false);
						break;
					case OpenDirectory:
						java.io.FileFilter[] fef = new java.io.FileFilter[] {Constant.ImageFileFilter};
						File[] files = new FileSearchUtil().search(file, fef);
						for(int i=0; i<files.length; i++)
							if(!addImageIcon(files[i].getPath()))
								break;
						break;
					case OpenFile:
						addImageIcon(file.getPath());
						break;
					default:
						JOptionPane.showMessageDialog(imageEditor, "Invalid open type.");
				}
				message.setText("Loading "+file.getPath()+" completed.");
				loading = false;
			}
		}
	}

	private boolean addImageIcon(String path)
	{	if(listPanel.getComponentCount() >= maxImageNumber)
		{ JOptionPane.showMessageDialog(imageEditor, "You cannot load more than "+Integer.toString(maxImageNumber)+" images");
			return false;
		}
		FileButton l = new FileButton(path);
		listPanel.add(l);
		listPanel.revalidate();

//		String warning = mem(path.substring(path.lastIndexOf("\\")+1));
//		if(warning == null)
			message.setText(path);
//		else
//			message.setText(warning);
		return true;
	}

	private int getListIndex(FileButton fb)
	{	for(int i=0; i<listPanel.getComponentCount(); i++)
		{	if(listPanel.getComponent(i).equals(fb))
				return i;
		}
		return 0;
	}

	class FileButton extends JButton implements ActionListener//, ImageObserver
	{	String path;
		Color defaultBackground;
		Border defaultBorder;
		boolean selected = false;
		Insets defaultInsets = new Insets(0,0,0,0);
		Image maxImage;
		FileButton fb;
		boolean originalImage;
		String loadingPath;
		MouseAdapter mouseAdapter;

		public void finalize()
		{	maxImage = null;
			fb = null;
			defaultBorder = null;
			defaultInsets = null;
			removeActionListener(this);
			removeMouseListener(mouseAdapter);
		}

		public void setOriginalImage(boolean b)
		{	originalImage = b;
		}

		public boolean isOriginalImage()
		{	return originalImage;
		}

		public FileButton(String aPath)
		{	super();
			fb = this;
			defaultBackground = getBackground();
			defaultBorder = getBorder();
			path = aPath;
			setMargin(defaultInsets);
			addActionListener(this);
			addMouseListener(mouseAdapter = new MouseAdapter()
			{	public void mouseClicked(MouseEvent event)
				{	if(	event.getModifiers() == InputEvent.BUTTON3_MASK &&
							event.getClickCount() == 1 )
					{	if(getBackground().equals(defaultBackground))
						{	setBackground(Color.blue);
							setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
							selected = true;
						}	else
						{	setBackground(defaultBackground);
							setBorder(defaultBorder);
							selected = false;
						}
					}
				}
			});

			loadingPath = path; //RandomAccessFileManager.getRandomAccessFileManager().create(path,"r",false);
			Image image = toolkit.createImage(loadingPath);
			if(!ImageUtilities.waitForImage(image))
			{	JOptionPane.showMessageDialog(imageEditor, "Load "+path+" error");
			}
			if(new File(loadingPath).length() > maxIconFileSize)
			{	try {
 					image = ImageUtilities.resizeImage(image,maxIconSize,maxIconSize);
					setOriginalImage(false);
				} catch(Exception e)
				{	setOriginalImage(true);
				}
			} else
			{	setOriginalImage(true);
			}
			createIcon(image);
		}

		public String getPath()
		{	return path;
		}

		public Image getImage()
		{	return maxImage;
		}

		private void createIcon(Image image)
		{	maxImage = image;
			resizeIcon();
			String msg = path;
			if(originalImage)
				msg += ",cache";
	   	setToolTipText(msg);
		}

		public void resizeIcon()
		{	ImageIcon icon = new ImageIcon();
			Image image = maxImage;
			int w = maxImage.getWidth(null);
			int h = maxImage.getHeight(null);
			if( w > defaultIconSize || h > defaultIconSize)
			{
/*			if(w > h)
				{	h = Math.max(1,defaultIconSize*h/w);
					w = defaultIconSize;
				} else
				{	w = Math.max(1,defaultIconSize*w/h);
					h = defaultIconSize;
				}
				image = maxImage.getScaledInstance(w,h,Image.SCALE_DEFAULT);
*/
				try{image = ImageUtilities.resizeImage(maxImage,defaultIconSize,defaultIconSize);} catch(Exception e){}
				ImageUtilities.waitForImage(image);
			}
			icon.setImage(image);
			super.setIcon(icon);
		}

		public boolean isSelected()
		{	return selected;
		}

		public void actionPerformed(ActionEvent ae)
		{	//currentIndex = getListIndex(this);
			Runnable r = new Runnable()
			{	public void run()
				{	source.setText(path);
					srcImageSource = path;
					setImage(fb);
				}
			};
			SwingUtilities.invokeLater(r);
		}
	}

	public void setImage(FileButton fb)
	{	try
		{		Image image = null;
				if(fb.isOriginalImage())
				{	image = fb.getImage(); //editImages.add(new ImageComponent(fb.getImage()));
				} else
				{	image = loadImage(fb.getPath());
				}
				ImageComponent ic = new PictureComponent(image);
				imagePanel.setImageComponent(ic);
				editableImageListPanel.add(ic);
		} catch(Exception e) {e.printStackTrace();}
	}

    class FilePreviewer extends JComponent implements PropertyChangeListener {
    	ImageIcon thumbnail = null;

    	public FilePreviewer(JFileChooser fc) {
    	    setPreferredSize(new Dimension(150, 75));
    	    fc.addPropertyChangeListener(this);
    	}

    	public void loadImage(File f) {
                if (f == null) {
                    thumbnail = null;
                } else {
    		ImageIcon tmpIcon = new ImageIcon(f.getPath());
    		if(tmpIcon.getIconWidth() > 140) {
    		    thumbnail = new ImageIcon(
    			tmpIcon.getImage().getScaledInstance(140, -1, Image.SCALE_DEFAULT));
    		} else {
    		    thumbnail = tmpIcon;
    		}
    	    }
    	}

    	public void propertyChange(PropertyChangeEvent e) {
    	    String prop = e.getPropertyName();
    	    if(prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
    		if(isShowing()) {
                        loadImage((File) e.getNewValue());
    		    repaint();
    		}
    	    }
    	}

    	public void paint(Graphics g) {
    	    if(thumbnail != null) {
    		int x = getWidth()/2 - thumbnail.getIconWidth()/2;
    		int y = getHeight()/2 - thumbnail.getIconHeight()/2;
    		if(y < 0) {
    		    y = 0;
    		}

    		if(x < 5) {
    		    x = 5;
    		}
    		thumbnail.paintIcon(this, g, x, y);
    	    }
    	}
        }

	public static void main(String[] args)
	{	
//		System.setProperty("jxsource.apps.AppHomeDir", "file:/c:/Users/NancyZhang/Pictures/ji_scan");
		try {
			String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
	    UIManager.setLookAndFeel(systemLookAndFeelClassName);
		} catch (Exception exc) {}
		ImageEditor iv = new ImageEditor();
		for(int i=0; i<args.length; i++)
		{ File f = new File(args[i]);
			if(f.exists())
			{	if(f.isDirectory())
					iv.open(f, 0);
				else
					iv.open(f, 2);
			} else
			{	System.out.println(f.getPath()+" does not exist.");
			}
		}
/*
		if(args.length > 0)
		{	while(iv.getCurrentIndex() == -1)
			{	try{Thread.currentThread().sleep(100);} catch(Exception e) {}
			}
			iv.displayIndex(iv.getCurrentIndex());
		}
*/
	}
}