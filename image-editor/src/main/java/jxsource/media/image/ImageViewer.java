package jxsource.media.image;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.filechooser.FileFilter;
//import jxsource.jxplorer.filesearch.*;
import java.lang.reflect.Method;
import javax.swing.text.*;
import java.beans.*;
import javax.swing.border.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.ImageObserver;
import com.sun.image.codec.jpeg.*;
//import jxsource.io.RandomAccessFileManager;
import jxsource.util.swing.ScreenSizeWindow;
import jxsource.util.swing.ScreenSizeWindowControll;
import jxsource.util.swing.CellListPanel;
import java.util.Vector;
import java.net.URL;
import jxsource.util.io.*;
import jxsource.util.io.filter.*;

public class ImageViewer extends JFrame implements ScreenSizeWindowControll
{	JPanel contentPane;
	ActionSearch actionSearch = new ActionSearch();
	ActionPauseAndResume actionPauseAndResume = new ActionPauseAndResume();
	ActionOpenDirectory actionOpenDirectory = new ActionOpenDirectory();
	ActionOpenFile actionOpenFile = new ActionOpenFile();
	ActionOpenList actionOpenList = new ActionOpenList();
	ActionExit actionExit = new ActionExit();
	ActionSaveSource actionSaveSource = new ActionSaveSource();
	ActionSaveProcessed actionSaveProcessed = new ActionSaveProcessed();
	ActionImageView actionImageView = new ActionImageView();
	ActionScreenSizeWindow actionScreenSizeWindow = new ActionScreenSizeWindow();
	ActionAnimation actionAnimation = new ActionAnimation();
//	ActionStopAnimation actionStopAnimation = new ActionStopAnimation();
	ActionNextImage actionNextImage = new ActionNextImage();
	ActionPrevImage actionPrevImage = new ActionPrevImage();
	ActionFirstImage actionFirstImage = new ActionFirstImage();
	ActionRemoveSelection actionRemoveSelection = new ActionRemoveSelection();
	ActionKeepSelection actionKeepSelection = new ActionKeepSelection();
	ActionSaveSelection actionSaveSelection = new ActionSaveSelection();
	ActionResizeIcon actionResizeIcon = new ActionResizeIcon();
	ActionMaxIconFileSize actionMaxIconFileSize = new ActionMaxIconFileSize();
	JFileChooser chooser;
	ImageViewer imageViewer;
	volatile CellListPanel listPanel;
	ImagePanel imagePanel;
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
	ScreenSizeWindow screenSizeWindow;
	Dimension nonScreenSizeImagePanelSize;
	Dimension nonScreenSizeImagePanelContainerSize;
	Rectangle nonScreenSizeImagePanelBounds;
	int nonScreenSizeDisplayType;

	CheckboxMenuItem itemDefault, itemFitWindow, itemZoom;
	File startDir;
	volatile boolean animation = false;
	volatile int currentIndex = -1;
	int minIconSize = 40;
	int maxIconSize = 120;
	int defaultIconSize = minIconSize;
	int maxIconFileSize = 10000;
	int maxImageNumber = 5000;

	int splitPaneDividerLocation = 205;

	static final int Search = 0;
	static final int OpenDirectory = 1;
	static final int OpenFile = 2;

	String imageSource;

	ImageViewerKeyAdapter imageViewerKeyAdapter = new ImageViewerKeyAdapter();
	ImageViewerMouseAdapter imageViewerMouseAdapter = new ImageViewerMouseAdapter();
	boolean fullScreenDisplay = false;

	int animationDuration = 2;

	Toolkit toolkit = Toolkit.getDefaultToolkit();
  private static final Insets shrinkwrap = new Insets(0,0,0,0);
	private int iconWidth = 16;
	private int iconHeight = 16;

	private int screenWidth;
	private int screenHeight;


	String strFile = "File";
	String strOpen = "Open";
	String strOpenList = "Open List";
	String strSearch = "Search";
	String strOpenDirectory = "Open Directory";
	String strOpenFile = "Open File";
	String strSave = "Save";
	String strSaveSourceImage = "Source Image";
	String strSaveProcessedImage = "Processed Image";
	String strSaveSelectedImage = "Save Selected Image";
	String strView = "View";
	String strDefaultView = "Default Size";
	String strFitWindow = "Fit Window";
	String strZoom = "Zoom ...";
	String strAnimation = "Animation";
	String strStart = "Start";
	String strStop = "Stop";
	String strStartAnimation = strStart;
	String strStopAnimation = strStop;
	String strNextImage = "Next Image";
	String strPreviousImage = "Previous Image";
	String strFirstImage = "First Image";
	String strScreenSizeWindow = "Screen Size Window";
	String strRestore = "Restore";
	String strResizeSelectionIcon = "Resize Selection Icon";
	String strEdit = "Edit";
	String strRemoveSelection = "Remove Selection";
	String strKeepSelection = "Keep Selection";
	String strOption = "Option";
	String strMaxIconFileSize = "Max Cacheable File Size ";
	String strReady = "ready";
	String strFailToLoadImage = "fail to load image";
	String strPause = "Pause ......";
	String strStopByUser = "Stopped by user's request.";
	String strStartSearch = "Start search image files in a directory and its sub-directories";
	String strStopSearch = "Stop search";
	String strExit = "Exit";
	String strTitle = "Image Viewer";

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
	ImageIcon openFileIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/file.gif");
	ImageIcon saveFileIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/save.gif");
	ImageIcon exitIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/exit.gif");
	ImageIcon windowSizeIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/WindowSize.gif");
	Icon animationIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/video.gif");
	Icon animationStopIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/video-disabled.gif");
	ImageIcon	prevImageIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/wht_prev.gif");
	ImageIcon nextImageIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/wht_next.gif");
	Icon pauseIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/pause.gif");
	Icon resumeIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/resume.gif");
	Icon stopIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/stop.gif");
	Icon openListIcon = ImageResourceUtilities.getImageIcon(Constant.HomeDir,"resource/image/FileOpen.gif");

	final int stop = 0;
	final int pause = 1;
	final int running = 2;
	volatile int loadingStatus = stop;
	Object sync = new Object();
	JButton pauseButton;
	JButton searchButton;
	MenuItem itemStartSearch;
	MenuItem itemStopSearch;
	JButton animationButton;
	MenuItem itemAnimation;
	MenuItem itemStopAnimation;
	MenuItem itemRemoveSelection;
	MenuItem itemKeepSelection;
	Menu menuEdit;
	
	public ImageViewer()
	{	
		String startDirPath = System.getProperty("jxsource.media.image.startSearchDir");
		if(startDirPath != null) {
			startDir = new File(startDirPath);
			if(!startDir.exists()) {
				System.err.println("Invalid System property: startSearchDir="+startDirPath);
				startDir = null;
			}
		}
		
		setSize(800,500);
		imageViewer = this;
		setTitle(strTitle);
		screenSizeWindow = new ScreenSizeWindow(this);
		screenSizeWindow.setControll(this);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = screenSize.width;
		screenHeight = screenSize.height;
/*
		try
		{	imageLoadAdapter = new ImageLoadAdapter();
			imageLoadAdapter.setImageWidth(screenWidth);
			imageLoadAdapter.setImageHeight(screenHeight);
		} catch(Exception e)
		{ e.printStackTrace();
		}
*/
/*
		KekListener has a focus problem and is not used.
		// used for java 1.3 when ScreenSizeWindow display only
		screenSizeWindow.addKeyListener(getImageViewerKeyAdapter());
		// used for java 1.4 when ScreenSizeWindow display
		// and all java when not ScreenSizeWindow display
		addKeyListener(getImageViewerKeyAdapter());
*/
		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());
		chooser = new JFileChooser();

		JToolBar toolbar = new JToolBar();
		contentPane.add(toolbar, BorderLayout.NORTH);

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

		JButton openListButton = new JButton();
		openListButton.addActionListener(getActionOpenDirectory());
		openListButton.setText(null);
		openListButton.setIcon(resizeToolBarIcon(openListIcon));
//   	openListButton.setToolTipText(openListToolTipText);
//   	openListButton.getAccessibleContext().setAccessibleName(openListAccessibleName);
		openListButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		openListButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		openListButton.setMargin(shrinkwrap);
		toolbar.add(openListButton);

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

		JButton saveFileButton = new JButton();
		saveFileButton.addActionListener(getActionSaveSource());
		saveFileButton.setText(null);
		saveFileButton.setIcon(resizeToolBarIcon(saveFileIcon));
   	saveFileButton.setToolTipText(saveFileToolTipText);
   	saveFileButton.getAccessibleContext().setAccessibleName(saveFileAccessibleName);
		saveFileButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		saveFileButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		saveFileButton.setMargin(shrinkwrap);
		toolbar.add(saveFileButton);

		JButton exitButton = new JButton();
		exitButton.addActionListener(getActionExit());
		exitButton.setText(null);
		exitButton.setIcon(resizeToolBarIcon(exitIcon));
   	exitButton.setToolTipText(exitToolTipText);
   	exitButton.getAccessibleContext().setAccessibleName(exitAccessibleName);
		exitButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		exitButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		exitButton.setMargin(shrinkwrap);
		toolbar.add(exitButton);

		JButton WindowSizeButton = new JButton();
		WindowSizeButton.addActionListener(getActionScreenSizeWindow());
		WindowSizeButton.setText(null);
		WindowSizeButton.setIcon(resizeToolBarIcon(windowSizeIcon));
   	WindowSizeButton.setToolTipText(windowSizeToolTipText);
   	WindowSizeButton.getAccessibleContext().setAccessibleName(windowSizeAccessibleName);
		WindowSizeButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		WindowSizeButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		WindowSizeButton.setMargin(shrinkwrap);
		toolbar.add(WindowSizeButton);

		animationButton = new JButton();
		animationButton.addActionListener(getActionAnimation());
		animationButton.setText(null);
		animationButton.setIcon(animationIcon = resizeToolBarIcon(animationIcon));
		animationStopIcon = resizeToolBarIcon(animationStopIcon);
   	animationButton.setToolTipText(animationToolTipText);
   	animationButton.getAccessibleContext().setAccessibleName(animationAccessibleName);
		animationButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		animationButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		animationButton.setMargin(shrinkwrap);
		toolbar.add(animationButton);

		JButton prevImageButton = new JButton();
		prevImageButton.addActionListener(getActionPrevImage());
		prevImageButton.setText(null);
		prevImageButton.setIcon(resizeToolBarIcon(prevImageIcon));
   	prevImageButton.setToolTipText(prevIamgeToolTipText);
   	prevImageButton.getAccessibleContext().setAccessibleName(prevIamgeAccessibleName);
		prevImageButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		prevImageButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		prevImageButton.setMargin(shrinkwrap);
		toolbar.add(prevImageButton);

		JButton nextImageButton = new JButton();
		nextImageButton.addActionListener(getActionNextImage());
		nextImageButton.setText(null);
		nextImageButton.setIcon(resizeToolBarIcon(nextImageIcon));
   	nextImageButton.setToolTipText(nextIamgeToolTipText);
   	nextImageButton.getAccessibleContext().setAccessibleName(nextIamgeAccessibleName);
		nextImageButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		nextImageButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		nextImageButton.setMargin(shrinkwrap);
		toolbar.add(nextImageButton);

		MenuBar menuBar = new MenuBar();
		setMenuBar(menuBar);

		Menu menuFile = new Menu(strFile);
		menuBar.add(menuFile);
		Menu menuOpen = new Menu(strOpen);
		menuFile.add(menuOpen);
		MenuItem itemOpenDirectory = new MenuItem(strOpenDirectory);
		itemOpenDirectory.addActionListener(getActionOpenDirectory());
		menuOpen.add(itemOpenDirectory);
		MenuItem itemOpenFile = new MenuItem(strOpenFile);
		itemOpenFile.addActionListener(getActionOpenFile());
		menuOpen.add(itemOpenFile);
		Menu menuSave = new Menu(strSave);
		menuFile.add(menuSave);
		MenuItem itemSaveSource = new MenuItem(strSaveSourceImage);
		menuSave.add(itemSaveSource);
		itemSaveSource.addActionListener(getActionSaveSource());
		MenuItem itemSaveProcessed = new MenuItem(strSaveProcessedImage);
		menuSave.add(itemSaveProcessed);
		itemSaveProcessed.addActionListener(getActionSaveProcessed());
		MenuItem itemSaveSelection = new MenuItem(strSaveSelectedImage);
		menuSave.add(itemSaveSelection);
		itemSaveSelection.addActionListener(getActionSaveSelection());
		MenuItem itemExit = new MenuItem(strExit);
		menuFile.add(itemExit);
		itemExit.addActionListener(getActionExit());

		Menu menuSearch = new Menu(strSearch);
		menuBar.add(menuSearch);
		itemStartSearch = new MenuItem(strStartSearch);
		itemStartSearch.addActionListener(getActionSearch());
		menuSearch.add(itemStartSearch);
		itemStopSearch = new MenuItem(strStopSearch);
		itemStopSearch.addActionListener(getActionSearch());
		itemStopSearch.setEnabled(false);
		menuSearch.add(itemStopSearch);


		Menu menuView = new Menu(strView);
		menuBar.add(menuView);

		itemDefault = new CheckboxMenuItem(strDefaultView);
		itemDefault.addItemListener(getActionImageView());
		itemDefault.setState(true);
		menuView.add(itemDefault);
		itemFitWindow = new CheckboxMenuItem(strFitWindow);
		itemFitWindow.addItemListener(getActionImageView());
		menuView.add(itemFitWindow);
		itemZoom = new CheckboxMenuItem(strZoom);
		itemZoom.addItemListener(getActionImageView());
		menuView.add(itemZoom);
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

		menuEdit = new Menu(strEdit);
		menuBar.add(menuEdit);
		itemRemoveSelection = new MenuItem(strRemoveSelection);
		itemRemoveSelection.addActionListener(getActionRemoveSelection());
		menuEdit.add(itemRemoveSelection);
		itemKeepSelection = new MenuItem(strKeepSelection);
		itemKeepSelection.addActionListener(getActionKeepSelection());
		menuEdit.add(itemKeepSelection);

		Menu menuOption = new Menu(strOption);
		menuBar.add(menuOption);
		MenuItem itemMaxIconFileSize = new MenuItem(strMaxIconFileSize);
		itemMaxIconFileSize.addActionListener(getActionMaxIconFileSize());
		menuOption.add(itemMaxIconFileSize);

		addWindowListener(new WindowAdapter()
		{	public void windowClosing(WindowEvent we)
			{	System.exit(0);
			}
		});
		listPanel = new CellListPanel(defaultIconSize,defaultIconSize,0,5);
		listPanel.setBackground(Color.white);
		JScrollPane listScroller = new JScrollPane(listPanel);
		listPanel.setContainer();

		imagePanel = new ImagePanel();
		plainPanel = new JPanel(new BorderLayout());
		plainPanel.add(imagePanel,BorderLayout.CENTER);
		imagePanel.setContainer(plainPanel);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			listScroller,
			plainPanel);
		imagePanel.setDisplayType(ImagePanel.DEFAULT);
		splitPane.setDividerSize(2);
		splitPane.setDividerLocation(splitPaneDividerLocation);

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


	public void installScreenSizeWindow()
	{	nonScreenSizeDisplayType  = imagePanel.getDisplayType();
		nonScreenSizeImagePanelSize  = imagePanel.getSize();
		nonScreenSizeImagePanelBounds  = imagePanel.getBounds();
		nonScreenSizeImagePanelContainerSize = splitPane.getRightComponent().getSize();

		if(nonScreenSizeDisplayType == ImagePanel.ZOOM)
		{	imageScroller.removeAll();
			splitPane.remove(imageScroller);
		} else
		{	plainPanel.remove(imagePanel);
			splitPane.remove(plainPanel);
		}
		screenSizeWindow.add(imagePanel);
		imagePanel.setContainer(screenSizeWindow);
		imagePanel.setDisplayType(ImagePanel.FIT_WINDOW);
		imagePanel.setBounds(screenSizeWindow.getBounds());
		imagePanel.adjustImageSize();
		screenSizeWindow.addMouseListener(getImageViewerMouseAdapter());
//		screenSizeWindow.addKeyListener(getImageViewerKeyAdapter());
//		addKeyListener(getImageViewerKeyAdapter());
		fullScreenDisplay = true;
		imagePanel.repaint();
	}

	public void uninstallScreenSizeWindow()
	{	screenSizeWindow.removeMouseListener(getImageViewerMouseAdapter());
//		screenSizeWindow.removeKeyListener(getImageViewerKeyAdapter());
//		removeKeyListener(getImageViewerKeyAdapter());

		screenSizeWindow.remove(imagePanel);
		imagePanel.setDisplayType(nonScreenSizeDisplayType);
		imagePanel.setBounds(nonScreenSizeImagePanelBounds);
		if(nonScreenSizeDisplayType == ImagePanel.ZOOM)
		{	imageScroller = new JScrollPane(imagePanel);
			imageScroller.setSize(nonScreenSizeImagePanelContainerSize);
			imagePanel.setContainer(imageScroller);
			splitPane.setRightComponent(imageScroller);
			splitPane.setDividerLocation(splitPaneDividerLocation);
		}
		else
		{	plainPanel = new JPanel(new BorderLayout());
			plainPanel.add(imagePanel,BorderLayout.CENTER);
			plainPanel.setSize(nonScreenSizeImagePanelContainerSize);
			plainPanel.validate();
			imagePanel.setContainer(plainPanel);
			splitPane.setRightComponent(plainPanel);
			splitPane.setDividerLocation(splitPaneDividerLocation);
		}
		imagePanel.adjustImageSize();
		fullScreenDisplay = false;
		imagePanel.repaint();

	}

	public Image loadImage(String path) throws Exception
	{	// to use jxsource
		srcImageSource = path; //RandomAccessFileManager.getRandomAccessFileManager().create(path,"r",false);

		Image image = toolkit.createImage(srcImageSource);
		if(!ImageUtilities.waitForImage(image))
			throw new IOException(strFailToLoadImage);
		mem("-- load image "+srcImageSource);
		return image;
	}

static int count = 0;
	private String mem(String info)
	{	Runtime runtime = Runtime.getRuntime();
		long total = runtime.totalMemory()/1000L;
		long free = runtime.freeMemory()/1000L;
		String mem = "memory: totle="+
											Long.toString(total)+"k ,free="+
											Long.toString(free)+"k";
//		System.out.println( Integer.toString(count++)+" - "+mem+". "+info);
		if(free*10L < total)
			return mem;
		else
			return null;
	}


	public ActionSearch getActionSearch()
	{	return actionSearch;
	}

	class ActionSearch extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
//			JButton searchButton = (JButton)ae.getSource();
			if(searchButton.getIcon() == searchIcon)
			{	// TODO
				//chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				if(startDir != null) {
					chooser.setCurrentDirectory(startDir);
				}
				int retval = chooser.showOpenDialog(imageViewer);
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

	public ActionOpenDirectory getActionOpenDirectory()
	{	return actionOpenDirectory;
	}

	class ActionOpenDirectory extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			int retval = chooser.showOpenDialog(imageViewer);
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
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			int retval = chooser.showOpenDialog(imageViewer);
			if(retval == JFileChooser.APPROVE_OPTION)
			{ File theFile = chooser.getSelectedFile();
	    	if(theFile != null)
				{	try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(theFile)));
						String line = null;
						while((line = reader.readLine()) != null) {
							String extension = line.substring(line.length()-3).toLowerCase();
							if(	extension.equals("gif") ||
									extension.equals("jpg"))
							{	extension = "."+extension;
							} else
							{	continue;
							}
							File temp = File.createTempFile("jxo",extension);
							FileOutputStream out = new FileOutputStream(temp);
							InputStream in = new URL(line).openStream();
							byte[] buf = new byte[32768];
							int n = 0;
							while((n = in.read(buf)) != -1)
							{	out.write(buf,0,n);
								out.flush();
							}
							in.close();
							out.close();
							temp.deleteOnExit();
							open(temp, OpenFile);
						}
					} catch(Exception e) {e.printStackTrace();}
	    	}
			}
		}
	}

	public ActionOpenFile getActionOpenFile()
	{	return actionOpenFile;
	}

	class ActionOpenFile extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			int retval = chooser.showOpenDialog(imageViewer);
			if(retval == JFileChooser.APPROVE_OPTION)
			{ File theFile = chooser.getSelectedFile();
	    	if(theFile != null)
				{	open(theFile, OpenFile);
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

	public void open(File theFile, int openType)
	{	try
		{	Thread thread = new Thread(new LoadRunnable(theFile, openType));
				thread.setPriority(Thread.MIN_PRIORITY);
					thread.start();
		} catch(Exception e) {e.printStackTrace();}
	}

	public ActionScreenSizeWindow getActionScreenSizeWindow()
	{	return actionScreenSizeWindow;
	}

	class ActionScreenSizeWindow extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
			if(!fullScreenDisplay)
				screenSizeWindow.display();
		}
	}

	public ActionImageView getActionImageView()
	{	return actionImageView;
	}

	class ActionImageView implements ItemListener {

		public void itemStateChanged(ItemEvent ae) {
			if(itemDefault == ae.getSource())
			{	itemFitWindow.setState(false);
				itemZoom.setState(false);
				itemDefault.setState(true);
				imagePanel.setDisplayType(ImagePanel.DEFAULT);
				setFitWindowPanel();
			} else
			if(ae.getSource() == itemFitWindow)
			{	itemDefault.setState(false);
				itemZoom.setState(false);
				itemFitWindow.setState(true);
				imagePanel.setDisplayType(ImagePanel.FIT_WINDOW);
				setFitWindowPanel();
			} else
			if(ae.getSource() == itemZoom)
			{	itemFitWindow.setState(false);
				itemDefault.setState(false);
				itemZoom.setState(true);
				setFixSizePanel();
				imagePanel.setDisplayType(ImagePanel.ZOOM);
			}
				imagePanel.adjustImageSize();
				// make imagePanel's container to repaint image
				// do not use imagePanel.repaint()
				imagePanel.setSize(imagePanel.getSize());
				imagePanel.repaint();

		}
	}

	private void setFitWindowPanel()
	{	Component rightComponent = splitPane.getRightComponent();
		if(rightComponent == plainPanel)
		{	return;
		} else
		{ Dimension d = rightComponent.getSize();
			imageScroller.removeAll();
			splitPane.remove(imageScroller);
			plainPanel = new JPanel(new BorderLayout());
			plainPanel.add(imagePanel,BorderLayout.CENTER);
			plainPanel.setSize(d);
			plainPanel.validate();
			imagePanel.setContainer(plainPanel);
			splitPane.setRightComponent(plainPanel);
			splitPane.setDividerLocation(splitPaneDividerLocation);
		}
	}

	private void setFixSizePanel()
	{	Component rightComponent = splitPane.getRightComponent();
		if(rightComponent == imageScroller)
		{	return;
		} else
		{ Dimension d = rightComponent.getSize(); //imagePanel.getSize();
			plainPanel.removeAll();
			splitPane.remove(plainPanel);
			imageScroller = new JScrollPane(imagePanel);
			imageScroller.setSize(d);
			imagePanel.setContainer(imageScroller);
			splitPane.setRightComponent(imageScroller);
			splitPane.setDividerLocation(splitPaneDividerLocation);

		}
	}

	public Action getActionSaveSource()
	{	return actionSaveSource;
	}

	class ActionSaveSource extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//			chooser.setDialogType(JFileChooser.SAVE_DIALOG);
			chooser.setFileFilter(Constant.ImageFileChooserFilter);
			int retval = chooser.showSaveDialog(imageViewer);
			if(retval == JFileChooser.APPROVE_OPTION)
			{ File theFile = chooser.getSelectedFile();
	    	if(theFile != null)
				{	if(theFile.exists())
					{	int ret = JOptionPane.showConfirmDialog(imageViewer,
											"Do you want to overwrite ?\n\n"+theFile.getPath(),
											"warning",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE);
						if(ret != 0) return;
					}
//System.out.println("** "+srcImageSource);
					File srcFile = new File(srcImageSource);
					if(!srcFile.exists())
					{	JOptionPane.showMessageDialog(imageViewer, srcImageSource+" does not exists.");
						return;
					}
					try
					{	InputStream in = new FileInputStream(srcImageSource);
						OutputStream out = new FileOutputStream(theFile);
						int n = 0;
						byte[] buf = new byte[4096];
						while((n = in.read(buf)) != -1)
							out.write(buf,0,n);
						out.flush();
						out.close();
						in.close();
					} catch(Exception e) {e.printStackTrace();}
				}
			}
		}
	}

	public ActionSaveProcessed getActionSaveProcessed()
	{	return actionSaveProcessed;
	}

	class ActionSaveProcessed extends AbstractAction {

		public void actionPerformed(ActionEvent ae) {

			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//			chooser.setDialogType(JFileChooser.SAVE_DIALOG);
			chooser.setFileFilter(new ExtensionFileChooserFilter("jpg", "JPEG file"));
			int retval = chooser.showSaveDialog(imageViewer);
			if(retval == JFileChooser.APPROVE_OPTION)
			{ File theFile = chooser.getSelectedFile();
	    	if(theFile != null)
				{	if(theFile.exists())
					{	int ret = JOptionPane.showConfirmDialog(imageViewer,
											"Do you want to overwrite ?\n\n"+theFile.getPath(),
											"warning",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE);
						if(ret != JOptionPane.YES_OPTION) return;
					}
					try
					{ OutputStream out = new FileOutputStream(theFile);
						JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
						BufferedImage outImage = ImageUtilities.makeBufferedImage(imagePanel.getAdjustSizeImage());
						encoder.encode(outImage);
						out.flush();
						out.close();
					} catch(Exception e) {e.printStackTrace();}
	    	}
			}

		}
	}

	public ActionAnimation getActionAnimation()
	{	return actionAnimation;
	}

	class ActionAnimation extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{	if(animationButton.getIcon() == animationIcon)
			{	animationButton.setIcon(animationStopIcon);
				itemAnimation.setEnabled(false);
				itemStopAnimation.setEnabled(true);
				animation();
			}	else
			{	animationButton.setIcon(animationIcon);
				itemAnimation.setEnabled(true);
				itemStopAnimation.setEnabled(false);
				stopAnimation();
			}
		}
	}

	private void animation()
	{	animation = true;
		message.setText("animation");
		Runnable r = new Runnable()
		{	public void run()
			{	while(animation)
				{ int i = getNextIndex();
					if(i != -1)
					{	FileButton fb = (FileButton) listPanel.getComponent(i);
						setImage(fb);//.getPath());
						source.setText(fb.getPath());
						try{Thread.currentThread().sleep(animationDuration * 1000);} catch(Exception e) {e.printStackTrace();}
					} else
					{	animation = false;
					}
				}
			}
		};
		new Thread(r).start();
	}
/*
	public ActionStopAnimation getActionStopAnimation()
	{	return actionStopAnimation;
	}

	class ActionStopAnimation extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{	stopAnimation();
		}
	}
*/
	private void stopAnimation()
	{	animation = false;
		message.setText("");
	}

	public ActionNextImage getActionNextImage()
	{	return actionNextImage;
	}

	class ActionNextImage extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{	nextImage();
		}
	}

	private void nextImage()
	{	int i = getNextIndex();
		if(i != -1)
		{	FileButton fb = (FileButton) listPanel.getComponent(i);
			setImage(fb);//.getPath());
			source.setText(fb.getPath());
		}
	}

	private int getNextIndex()
	{	if(listPanel.getComponentCount() == 0)
			currentIndex = -1;
		else if(currentIndex < listPanel.getComponentCount()-1)
			currentIndex++;
		else
			currentIndex = 0;
		return currentIndex;
	}

	public ActionPrevImage getActionPrevImage()
	{	return actionPrevImage;
	}

	class ActionPrevImage extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{	prevImage();
		}
	}

	private void prevImage()
	{	int i = getPrevIndex();
		if(i != -1)
		{	FileButton fb = (FileButton) listPanel.getComponent(i);
			setImage(fb);//.getPath());
			source.setText(fb.getPath());
		}
	}

	private int getPrevIndex()
	{	if(listPanel.getComponentCount() == 0)
			currentIndex = -1;
		else if(currentIndex > 0)
			currentIndex--;
		else
			currentIndex = listPanel.getComponentCount()-1;
		return currentIndex;
	}

	public ActionFirstImage getActionFirstImage()
	{	return actionFirstImage;
	}

	class ActionFirstImage extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{	firstImage();
		}
	}

	private void firstImage()
	{	if(listPanel.getComponentCount() > 0)
		{	currentIndex = 0;
			FileButton fb = (FileButton) listPanel.getComponent(currentIndex);
			setImage(fb);//.getPath());
			source.setText(fb.getPath());
		}
	}

	public ActionSaveSelection getActionSaveSelection()
	{	return actionSaveSelection;
	}

	class ActionSaveSelection extends AbstractAction {

		public void actionPerformed(ActionEvent ae)
		{ if(listPanel.getComponentCount() > 0)
			{
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				chooser.setFileFilter(Constant.ImageFileChooserFilter);
				int retval = chooser.showSaveDialog(imageViewer);
				if(retval == JFileChooser.APPROVE_OPTION)
				{ final File theFile = chooser.getSelectedFile();
	  	  	if(theFile != null)
					{	int i=0;
						while(i<listPanel.getComponentCount())
						{	FileButton fb = (FileButton)listPanel.getComponent(i++);
							if(fb.isSelected())
							{	File src = new File(fb.getPath());
								File target = new File(theFile,src.getName());
								if(target.exists())
								{	int ret = JOptionPane.showConfirmDialog(imageViewer,
											"Do you want to overwrite ?\n\n"+theFile.getPath(),
											"warning",
											JOptionPane.QUESTION_MESSAGE,
											JOptionPane.YES_NO_OPTION);
									if(ret != 0) return;
								}
								try
								{	InputStream in = new FileInputStream(src);
									OutputStream out = new FileOutputStream(target);
									int n = 0;
									byte[] buf = new byte[8192];
									while((n = in.read(buf)) != -1)
										out.write(buf,0,n);
									out.flush();
									out.close();
									in.close();
								} catch(Exception e) {e.printStackTrace();}
							}
						}
					}
				}
			}
		}
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
				currentIndex = resetCurrentIndex();
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
				currentIndex = resetCurrentIndex();
				listPanel.revalidate();
				listPanel.repaint();
			}
		}
	}

	protected int resetCurrentIndex()
	{	if(listPanel.getComponentCount() == 0)
		{ currentIndex = -1;
		} else
		if(imageSource == null)
		{	currentIndex = 0;
		}	else
		{	currentIndex = 0;
			for(int i=0; i<listPanel.getComponentCount(); i++)
			{	if(((FileButton)listPanel.getComponent(i)).getPath().equals(imageSource))
				{	currentIndex = i;
					break;
				}
			}
		}
		return currentIndex;
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
			if(JOptionPane.showConfirmDialog(imageViewer, panel) == JOptionPane.YES_OPTION)
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

	public ActionMaxIconFileSize getActionMaxIconFileSize()
	{	return actionMaxIconFileSize;
	}

	class ActionMaxIconFileSize implements ActionListener {

		public void actionPerformed(ActionEvent ae) {
			JTextField text = new JTextField(Integer.toString(maxIconFileSize));
			JOptionPane.showMessageDialog(imageViewer, text,"Enter max cacheable file size",JOptionPane.QUESTION_MESSAGE);
			try {
				maxIconFileSize = Integer.parseInt(text.getText());
			}	catch(Exception e)
			{	JOptionPane.showMessageDialog(imageViewer, e.getClass().getName()+": "+e.getMessage());
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
				switch(openType)
				{	case Search:
						try
						{	java.io.FileFilter[] fef = new java.io.FileFilter[] {Constant.ImageFileFilter};
							BufferedReader in = new BufferedReader(new InputStreamReader(
								new FileSearchUtil().recursiveSearch(file,fef)));
							String path = null;
							String dir = "";
							while((path = in.readLine()) != null && addImageIcon(path))
							{	if(loadingStatus == pause)
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
						{	JOptionPane.showMessageDialog(imageViewer, "Load "+file.getPath()+" error:\n\n"+e.getClass().getName()+":"+e.getMessage());
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
						JOptionPane.showMessageDialog(imageViewer, "Invalid open type.");
				}
				if(currentIndex == -1 && listPanel.getComponentCount() > 0)
					currentIndex = 0;
				message.setText("Loading "+file.getPath()+" completed.");
				loading = false;
			}
		}
	}

	private boolean addImageIcon(String path)
	{	if(listPanel.getComponentCount() >= maxImageNumber)
		{ JOptionPane.showMessageDialog(imageViewer, "You cannot load more than "+Integer.toString(maxImageNumber)+" images");
			return false;
		}
		FileButton l = new FileButton(path);
		String warning = mem(path.substring(path.lastIndexOf("\\")+1));
		if(warning == null)
			message.setText(path);
		else
			message.setText(warning);
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
		int height;
		int width;
		
		public int getImageHeight() {
			return height;
		}

		public int getImageWidth() {
			return width;
		}
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
			{	
				if(imageViewer.getClass().getSimpleName().equals("ImageViewer")) {
					JOptionPane.showMessageDialog(imageViewer, "Load "+path+" error");
				} else {
					// TODO: add action from extended class
				}
			}
			height = image.getHeight(null);
			width = image.getWidth(null);
//			System.out.println(width+"*"+height+": "+path);
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
			listPanel.add(this);
			listPanel.revalidate();
			currentIndex = listPanel.getComponentCount()-1;
		}

		public void resizeIcon()
		{	ImageIcon icon = new ImageIcon();
			Image image = maxImage;
			int w = maxImage.getWidth(null);
			int h = maxImage.getHeight(null);
			if( w > defaultIconSize || h > defaultIconSize)
			{	if(w > h)
				{	h = Math.max(1,defaultIconSize*h/w);
					w = defaultIconSize;
				} else
				{	w = Math.max(1,defaultIconSize*w/h);
					h = defaultIconSize;
				}
				image = maxImage.getScaledInstance(w,h,Image.SCALE_DEFAULT);
				ImageUtilities.waitForImage(image);
			}
			icon.setImage(image);
			super.setIcon(icon);
		}
/*
		private void createMaxImage(Image image)
		{

			int w = image.getWidth(null);
			int h = image.getHeight(null);
			if( w > maxIconSize || h > maxIconSize)
			{	if(w > h)
				{	h = Math.max(1,maxIconSize*h/w);
					w = maxIconSize;
				} else
				{	w = Math.max(1,maxIconSize*w/h);
					h = maxIconSize;
				}
//				maxImage = image.getScaledInstance(w,h,Image.SCALE_DEFAULT);
			}
			maxImage = image.getScaledInstance(w,h,Image.SCALE_DEFAULT);
			if(!ImageUtilities.waitForImage(maxImage))

				maxImage = image;
		}
*/
		public boolean isSelected()
		{	return selected;
		}

		public void actionPerformed(ActionEvent ae)
		{	currentIndex = getListIndex(this);
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

	public void displayIndex(int listIndex)
	{	FileButton fb = (FileButton) listPanel.getComponent(listIndex);
		setImage(fb); //fb.getPath());
		source.setText(fb.getPath());
		source.revalidate();
	}

	public void setImage(FileButton fb)
	{	try
		{//	synchronized(loadLock) {
				if(fb.isOriginalImage())
				{	imagePanel.setImage(fb.getImage());
				} else
				{	Image i = loadImage(fb.getPath());
					imagePanel.setImage(i);//fb.getImage());
				}
			//}
			imageSource = fb.getPath();
		} catch(Exception e) {e.printStackTrace();}
	}

	public int getCurrentIndex()
	{	return currentIndex;
	}

	public ImageViewerKeyAdapter getImageViewerKeyAdapter()
	{	return imageViewerKeyAdapter;
	}

	class ImageViewerKeyAdapter extends KeyAdapter
	{ public void keyPressed(KeyEvent e)
		{	//System.out.println(e.getKeyCode());
			switch(e.getKeyCode())
			{	case KeyEvent.VK_ESCAPE:
					if(fullScreenDisplay)
						screenSizeWindow.dispose();
					break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_PAGE_UP:
					prevImage();
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_PAGE_DOWN:
					nextImage();
					break;
				case KeyEvent.VK_HOME:
					firstImage();
					break;
				case KeyEvent.VK_SPACE:
				case KeyEvent.VK_ENTER:
					animation();
					break;
				case KeyEvent.VK_END:
					stopAnimation();
					break;
			}
		}
	}

	public ImageViewerMouseAdapter getImageViewerMouseAdapter()
	{	return imageViewerMouseAdapter;
	}

	class ImageViewerMouseAdapter extends jxsource.util.awt.event.MouseAdapter {
		public ImageViewerMouseAdapter()
		{	super(300);
		}

    public void _mouseClicked ( MouseEvent event ) {
				if(	event.getModifiers() == InputEvent.BUTTON1_MASK &&
						event.getClickCount() == 2)
					screenSizeWindow.dispose();
				else
				if(	event.getModifiers() == InputEvent.BUTTON1_MASK &&
						event.getClickCount() == 1)
				{	if(animation)
						stopAnimation();
					else
						animation();
				}
				else
				if(	event.getModifiers() == InputEvent.BUTTON3_MASK &&
						event.getClickCount() == 2)
					prevImage();
				else
				if(	event.getModifiers() == InputEvent.BUTTON3_MASK &&
						event.getClickCount() == 1)
					nextImage();
    }
  };



	public static void main(String[] args)
	{	try {
			String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
	    UIManager.setLookAndFeel(systemLookAndFeelClassName);
		} catch (Exception exc) {}
		ImageViewer iv = new ImageViewer();
		for(int i=0; i<args.length; i++)
		{ File f = new File(args[i]);
System.out.println(f);
			if(f.exists())
			{	if(f.isDirectory())
					iv.open(f, ImageViewer.OpenDirectory);
				else
					iv.open(f, 2);
			} else
			{	System.out.println(f.getPath()+" does not exist.");
			}
		}
/*		if(args.length > 0)
		{	while(iv.getCurrentIndex() == -1)
			{	try{Thread.currentThread().sleep(100);} catch(Exception e) {}
			}
			iv.displayIndex(iv.getCurrentIndex());
		}
*/	}
}
