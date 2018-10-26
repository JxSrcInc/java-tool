package jxsource.apps.expense;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import jxsource.apps.expense.util.CSVFileParser;
import jxsource.apps.expense.util.CurrencyRenderer;
import jxsource.apps.expense.util.FileUtil;
//import jxsource.util.io.filter.ExtensionFileChooserFilter;
import jxsource.util.swing.objectview.ObjectTable;

// Referenced classes of package jxsource.apps.assetmanagement.rental:
//            Expenses, ExpenseCategories, Expense, ExpenseCategory, 
//            ImportFile, Constant, ExpenseFactory, ExpensesView

public class ExpenseManager extends JFrame
{
    class TablePane extends JPanel
    {

        JLabel total;
        ExpenseCategories categories;
        ObjectTable table;
        JButton bttnDelete;

        protected void delete(int i)
        {
            ExpenseCategory expensecategory = (ExpenseCategory)categories.getObject(i);
            if(expensecategory.amount != 0)
            {
            	JOptionPane.showMessageDialog(this, strCannotDeleteCategory);
            	return;
            }
            if(expensecategory.view != null)
            {
                expensecategory.view.dispose();
            }
            categories.remove(i);
            categories.fireTableRowsDeleted(i, i);
            updateTotal();
        }

        protected void updateTotal()
        {
            double d = categories.calculateTotal();
            if(d < 0.0D)
            {
                total.setForeground(Color.red);
            } else
            {
                total.setForeground(Color.black);
            }
            total.setText(NumberFormat.getCurrencyInstance().format(d));
        }

        private int findIndexByView(Object obj)
        {
            for(int i = 0; i < categories.getRowCount(); i++)
            {
                if(((ExpenseCategory)categories.getObject(i)).view.getExpenses().equals(obj))
                {
                    return i;
                }
            }

            return -1;
        }


        public TablePane(ExpenseCategories expensecategories, String s)
        {
            bttnDelete = new JButton(strDelete);
            categories = expensecategories;
            table = new ObjectTable(categories,TransferHandler.MOVE);
            table.setDragEnabled(false);
            table.getColumnModel().getColumn(categories.getIndexOfColumn(strAmount)).setCellRenderer(new CurrencyRenderer());
            setLayout(new BorderLayout());
            add("Center", new JScrollPane(table));
            JLabel jlabel = new JLabel("  " + s);
            add("North", jlabel);
            JPanel jpanel = new JPanel(new BorderLayout());
            JToolBar jtoolbar = new JToolBar();
            jtoolbar.add(new JLabel(strTotal + ": "));
            jtoolbar.add(total = new JLabel());
            jpanel.add("Center", jtoolbar);
            jpanel.add("East", bttnDelete);
            add("South", jpanel);
            bttnDelete.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent actionevent)
                {
                    int i = table.getSelectedRow();
                    if(i >= 0 && 0 == JOptionPane.showConfirmDialog(table, strDeleteMessage + " " + ((ExpenseCategory)categories.getObject(i)).category + "?"))
                    {
                        delete(i);
                    }
                }

            });
            updateTotal();
            categories.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent propertychangeevent)
                {
                    updateTotal();
                }

            });
            table.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent mouseevent)
                {
                	if(mouseevent.getButton() != 1)
                    {
                        int i = table.getSelectedRow();
                        if(i >= 0)
                        {
                            ExpenseCategory expensecategory = (ExpenseCategory)categories.getObject(i);
                            if(expensecategory.view != null)
                            {
                                expensecategory.view.setVisible(true);
                            }
                        }
                    }
                }

            });
        }
    }


    ExpenseCategories expenseCategories;
    ExpenseCategories importCategories;
    String strAmount;
    String strTotal;
    String strNew;
    String strTitle;
    String strImport;
    String strFile;
    String strRepository;
    String strLoad;
    String strSave;
    String strSaveAs;
    String strExportAsCsv;
    String strCategory;
    String strSaveMessage;
    String strExit;
    String strDelete;
    String strDeleteMessage;
    String strExportSummary;
    String strCannotDeleteCategory = "Cannot delete a non-empty categor.";
    public static final String strDefaultCategory = "";//"Please specify the name of category";
    public static final String strImportCategory = "Import";
    TablePane categoryPane;
    TablePane importPane;
    File repository;
    JFileChooser fileChooser;
    Expenses loadedExpenses;
//    ExtensionFileChooserFilter extFilter = new ExtensionFileChooserFilter(new String[]{"csv"});

    String strBusiness = "Business Expenses";
    String strRental = "Rental Expenses";
    String[] templateBusiness = new String[] {
    		"purchase cost",
    		"sale expense",
    		"rent",
    		"improvement for sale",
    		"utility",
    		"insurance",
    		"property tax",
    		"other tax",
    		"professional (Termite)",
    		"mortgage interest",
    		"management fee",
    		"repair"
    		};
    String[] templateRental = new String[] {
    		"Advertising",
    		"Cleaning/Maintain",
    		"Insurance",
    		"Commissions",
    		"Mortgage Insurance",
    		"Professional Fees",
    		"Management Fees",
    		"Repairs",
    		"Supplies",
    		"Real Estate Taxes",
    		"OtherTaxes",
    		"Utilities",
    		"Legal and Professional Fees",
    		"Interests",
    		"Rent"
    };
    String[] templateNewRental = new String[] {
    		"Abstract and Recording Fees",
    		"Legal Fees/Title Search/Document Preparation",
    		"Land Surveys",
    		"Title Insurance",
    		"Transfer or Stamp Taxes",
    		"Expenses You Paid for the Seller"
    };
    public ExpenseManager()
    {
        strAmount = "amount";
        strTotal = "Total";
        strNew = "New";
        strTitle = "Expense Manager";
        strImport = "Import csv and xls file";
        strFile = "File";
        strRepository = "Repository";
        strLoad = "Load from repository";
        strSave = "Save";
        strSaveAs = "SaveAs";
        strExportAsCsv = "ExportAsCsv";
        strExportSummary = "ExportSummary";
        strCategory = "Category";
        strSaveMessage = "Do you want to save category data?";
        strExit = "Exit";
        strDelete = "Delete";
        strDeleteMessage = "Do you want to delete";
        fileChooser = new JFileChooser();
        loadedExpenses = new Expenses();
        int c = 150;
        int c1 = 300;
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(c1, c, dimension.width - c1 * 2, dimension.height - c * 2);
        setTitle(strTitle);
        setDefaultCloseOperation(0);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent)
            {
                exit();
            }

        });
//        FileLoaderFactory.setNameOfFileLoaderClass(Constant.nameOfCSVFileLoaderClass);
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        expenseCategories = new ExpenseCategories();
        importCategories = new ExpenseCategories();
        JSplitPane jsplitpane = new JSplitPane(1, categoryPane = new TablePane(expenseCategories, strCategory), importPane = new TablePane(importCategories, strImport));
        jsplitpane.setDividerLocation(dimension.width/2-c1);
        jsplitpane.setDividerSize(2);
        container.add("Center", jsplitpane);
        JMenuBar jmenubar = new JMenuBar();
        setJMenuBar(jmenubar);
        JMenu jmenu = new JMenu(strFile);
        jmenubar.add(jmenu);
        JMenuItem jmenuitem = new JMenuItem(strImport);
        jmenuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                importExpenses();
            }

        });
        jmenu.add(jmenuitem);
        JMenuItem jmenuitem1 = new JMenuItem(strLoad);
        jmenuitem1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                loadRepo();
            }

        });
        jmenu.add(jmenuitem1);
        JMenuItem jmenuitem2 = new JMenuItem(strSave);
        jmenuitem2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                saveRepo();
            }

        });
        jmenu.add(jmenuitem2);
        JMenuItem jmenuitem2_1 = new JMenuItem(strSaveAs);
        jmenuitem2_1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                saveAs();
            }

        });
        jmenu.add(jmenuitem2_1);
        JMenuItem jmenuitem2_2 = new JMenuItem(strExportAsCsv);
        jmenuitem2_2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                exportAsCsv();
            }

        });
        jmenu.add(jmenuitem2_2);
        JMenuItem jmenuitem2_3 = new JMenuItem(strExportSummary);
        jmenuitem2_3.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                exportSummary();
            }

        });
        jmenu.add(jmenuitem2_3);

        JMenuItem jmenuitem3 = new JMenuItem(strExit);
        jmenuitem3.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                exit();
            }

        });
        jmenu.add(jmenuitem3);
        JMenu jmenu1 = new JMenu(strCategory);
        jmenubar.add(jmenu1);
        JMenuItem jmenuitem4 = new JMenuItem(strNew);
        jmenuitem4.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                createCategory(strDefaultCategory);
            }

        });
        jmenu1.add(jmenuitem4);
        JMenuItem jmenuitem5 = new JMenuItem(strBusiness);
        jmenuitem5.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
            	for(String name: templateBusiness)
            	{
            		createCategory(name);
            	}
            }

        });
        jmenu1.add(jmenuitem5);
        JMenuItem jmenuitem6 = new JMenuItem(strRental);
        jmenuitem6.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
            	for(String name: templateRental)
            	{
            		createCategory(name);
            	}
            }

        });
        jmenu1.add(jmenuitem6);
        JMenuItem jmenuitem7 = new JMenuItem("Buying Cost");
        jmenuitem7.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
            	for(String name: templateNewRental)
            	{
            		createCategory(name);
            	}
            }

        });
        jmenu1.add(jmenuitem7);
    }

    public void exit()
    {
        int i = JOptionPane.showConfirmDialog(this, strSaveMessage, strSave, 1);
        if(i == 2)
        {
            return;
        }
        if(i == 0 && !saveRepo())
        {
            return;
        } else
        {
            System.exit(0);
            return;
        }
    }

    private File getHomeDir() {
    	String home = System.getProperty("RentalHome");
		if(home == null)
			home = Constant.Home;
		return new File(home);
    }
    public void loadRepo()
    {	
//    	String home = System.getProperty("RentalHome");
//		if(home == null)
//			home = Constant.Home;
		fileChooser.setCurrentDirectory(getHomeDir());
		//fileChooser.addChoosableFileFilter(extFilter);
        int i = fileChooser.showOpenDialog(this);
        if(i == 0 && fileChooser.getSelectedFile() != null)
        {
            loadRepo(fileChooser.getSelectedFile());
        } else
        {
            return;
        }
    }

		// load data from repository
    public void loadRepo(File file)
    {
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            repository = file;
            setTitle(strTitle+": "+file.getName());
            String s = null;
            Expenses expenses = null;
            while((s = bufferedreader.readLine()) != null) 
            {
            	if(s.trim().length() == 0) {
            		continue;
            	}
                String as[] = CSVFileParser.parse(s);
                if(as.length == 1)
                {
                    if(expenses != null)
                    {
                        expenses.fireTableRowsInserted(0, expenses.getRowCount() - 1);
                        expenses.calculateTotal();
                        removeFromImportCategory(expenses);
                    }
                    ExpenseCategory expensecategory = expenseCategories.getExpenseCategoryByName(as[0]);
                    if(expensecategory == null)
                    {
                        ExpensesView expensesview = ExpenseFactory.createExpensesView(as[0]);
                        expensecategory = createCategory(as[0]);
                        expensecategory.view = expensesview;
                        expenses = expensesview.getExpenses();
                        expenses.addPropertyChangeListener(expenseCategories);
                    } else
                    {
                        expenses = expensecategory.view.getExpenses();
                    }
                } else
                {
                    Expense expense = (new Expense()).set(s);
                    if(!expenses.contains(expense))
                    {
                        expenses.load(expense);
                    }
                }
            }
            expenses.fireTableRowsInserted(0, expenses.getRowCount() - 1);
            expenses.calculateTotal();
            removeFromImportCategory(expenses);
            bufferedreader.close();
        }
        catch(Exception exception)
        {
        	exception.printStackTrace();
            String s1 = exception.getClass().getName() + "\n" + exception.getMessage();
            JOptionPane.showMessageDialog(this, s1);
        }
    }

    protected void removeFromImportCategory(Expenses expenses)
    {
        for(int i = 0; i < importCategories.getRowCount(); i++)
        {
            Expenses expenses1 = ((ExpenseCategory)importCategories.getObject(i)).view.getExpenses();
            for(int j = expenses.getRowCount() - 1; j >= 0; j--)
            {
                int k = expenses1.getIndexOfObject(expenses.getObject(j));
                if(k >= 0)
                {
                    expenses1.remove(k);
                }
            }

            expenses1.calculateTotal();
            expenses1.fireTableDataChanged();
        }

    }

    public void saveAs()
    {
		fileChooser.setCurrentDirectory(getHomeDir());
        int i = fileChooser.showSaveDialog(this);
        if(i == 0 && fileChooser.getSelectedFile() != null)
        {
            repository = fileChooser.getSelectedFile();
            saveRepo();
        } else
        {
            return;
        }
    }

    public void exportAsCsv()
    {
		fileChooser.setCurrentDirectory(getHomeDir());
        int i = fileChooser.showSaveDialog(this);
        if(i == 0 && fileChooser.getSelectedFile() != null)
        {
            File exportAsCsvFile = fileChooser.getSelectedFile();
            if(exportAsCsvFile != null) {
    			try {
    		        if(exportAsCsvFile.exists())
    		        {
    		            FileUtil.backFile(exportAsCsvFile);
    		        }
    		        Expenses loadedExpenses = this.getLoadedExpenses();
    		        loadedExpenses.exportToCsv(new FileOutputStream(exportAsCsvFile));
    			} catch(Exception e)
    			{ 
    				String s = e.getClass().getName() + "\n" + e.getMessage();
    				JOptionPane.showMessageDialog(this, s);
    			}

            }
        } else
        {
            return;
        }
    }

    public void exportSummary()
    {
    	    	
		fileChooser.setCurrentDirectory(getHomeDir());
        int i = fileChooser.showSaveDialog(this);
        if(i == 0 && fileChooser.getSelectedFile() != null)
        {
            File exportSummaryFile = fileChooser.getSelectedFile();
            if(exportSummaryFile != null) {
    			try {
    		        if(exportSummaryFile.exists())
    		        {
    		            FileUtil.backFile(exportSummaryFile);
    		        }
    		        FileOutputStream out = new FileOutputStream(exportSummaryFile);
    		        for(int k=0; k<expenseCategories.getRowCount(); k++) {
    		        	Object name = expenseCategories.getValueAt(k,0);
    		        	double amount = (Double)expenseCategories.getValueAt(k,1);
    		        	String s = "";
    		        	if(amount < 0) {
    		        		s = String.format("% 8.2f,  %s\n",-amount,  name);
    		        	} else if(amount > 0) {
    		        		s = String.format("% 8.2f,  %s\n",amount,  name);        		
    		        	} else {
    		        		continue;
    		        	}
    		        	out.write(s.getBytes());
    		        	out.flush();
    		        }
    		        out.close();
    			} catch(Exception e)
    			{ 
    				String s = e.getClass().getName() + "\n" + e.getMessage();
    				JOptionPane.showMessageDialog(this, s);
    			}

            }
        }
    }


    public boolean saveRepo()
    {
        BufferedWriter bufferedwriter = null;
        if(repository == null)
        {
    		fileChooser.setCurrentDirectory(getHomeDir());
            int i = fileChooser.showSaveDialog(this);
            if(i == 0 && fileChooser.getSelectedFile() != null)
            {
                repository = fileChooser.getSelectedFile();
            } else
            {
                return false;
            }
        }
			try {
        if(repository.exists())
        {
            FileUtil.backFile(repository);
        }
        bufferedwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repository)));
        for(int j = 0; j < expenseCategories.getRowCount(); j++)
        {
            Object obj = expenseCategories.getObject(j);
            if(obj != null)
            {
                ExpenseCategory expensecategory = (ExpenseCategory)obj;
                if(expensecategory.view != null)
                {
                    print(bufferedwriter, expensecategory.view.getTitle());
                    Expenses expenses = expensecategory.view.getExpenses();
                    for(int k = 0; k < expenses.getRowCount(); k++)
                    {
                        print(bufferedwriter, expenses.getObject(k).toString());
                    }

                }
            }
        }
				bufferedwriter.close();
        return true;
			} catch(Exception e)
			{ String s = e.getClass().getName() + "\n" + e.getMessage();
        JOptionPane.showMessageDialog(this, s);
			}
			if(bufferedwriter != null)
        {
            try
            {
                bufferedwriter.close();
            }
            catch(IOException ioexception1) { }
        }
			return false;
    }

    protected void print(BufferedWriter bufferedwriter, String s)
        throws IOException
    {
        bufferedwriter.write(s, 0, s.length());
        bufferedwriter.newLine();
        bufferedwriter.flush();
    }

		// import not processes data file 
    public void importExpenses()
    {
        ImportFile importfile = new ImportFile();
        ExpensesView expensesview = ExpenseFactory.createExpensesView("");
        Expenses expenses = expensesview.getExpenses();
        try
        {
        	//import raw data from file to expenses object
            if(importfile.load(null, expenses))
            {
            	// check duplicates
                String importFileName = importfile.getFile().getName();
                expenses.sortAllColumns();
                // check duplicates in the expenses just loaded
                // using equals() method
                for(int i = 0; i < expenses.getRowCount() - 1; i++)
                {
                    Expense expense = (Expense)expenses.getObject(i);
                    Expense expense1 = (Expense)expenses.getObject(i + 1);
                    if(expense.equals(expense1))
                    {
                    	System.out.println(expense);
                        expense.attributes = "D";
                        expense1.attributes = "D";
                    }
                }

                // check duplicates with loaded importExpenses
                // using isDuplicate() method
                Expenses loadedImports = this.getLoadedImport();
                for(int j = expenses.getRowCount() - 1; j >= 0; j--)
                {
                    if(loadedImports.isDuplicate(expenses.getObject(j)))
                    {
//                        expenses.remove(j);
                    	Expense expense = (Expense)expenses.getObject(j);
                    	expense.attributes = "D";
                    }
                }
                
                // update based on loaded Expenses
                Expenses loadedExpenses = this.getLoadedExpenses();
                for(int j = expenses.getRowCount() - 1; j >= 0; j--)
                {
                	Expense matched = loadedExpenses.getMatch(expenses.getObject(j));
                    if(matched != null)
                    {
                    	expenses.remove(j);
                    	/*
                    	//System.out.println("** "+matched+","+matched.attributes);
                    	if(matched.attributes.toUpperCase().indexOf("D") == -1) {
                    		// remove expense if it is loaded but not duplicate
                            System.out.println("ExpenseManager.remove: "+expenses.remove(j));                           		
                    	} else {
                    		// set expense as duplicate because it is 
                    		// marked as duplicate in loaded expenses
                    		Expense expense = (Expense)expenses.getObject(j);
                    		expense.attributes = "D";
                    		System.out.println("ExpenseManager.setDuplicate: "+expense);
                    	}
                    	*/
                    } else {
                    	Expense e1 = expenses.getObject(j);
                    	System.out.println("unmatched: "+e1);
                    	Expense e2 = loadedExpenses.getMerchant(((Expense)expenses.getObject(j)).merchant);
                    	System.out.println("unmatched: "+e2);
                    	System.out.println(e1.equals(e2));
                    }
                }
                

                // load importCategories -- category shown in import table
                ExpenseCategory expensecategory = new ExpenseCategory(importFileName, expenses.getTotalExpense(), expensesview);
                expenses.addPropertyChangeListener(importCategories);
                importCategories.load(expensecategory);
                importCategories.calculateTotal();
                importCategories.fireTableRowsInserted(importCategories.getRowCount() - 1, importCategories.getRowCount() - 1);
                expensesview.setTitle("Import: " + importFileName);
                expensesview.show();
            }
        }
        catch(Exception exception)
        {
        	exception.printStackTrace();
            String s1 = exception.getClass().getName();
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            exception.printStackTrace(new PrintStream(bytearrayoutputstream));
            JOptionPane.showMessageDialog(this, bytearrayoutputstream.toString(), s1, 0);
        }
    }

    protected ExpenseCategory createCategory(String s)
    {
        ExpenseCategory expensecategory = new ExpenseCategory(s);
        expenseCategories.add(expensecategory);
        // if category name is not empty
        if(s.length() > 0)
        {
        	expensecategory.view = ExpenseFactory.createExpensesView(s);
        	expensecategory.view.getExpenses().addPropertyChangeListener(expenseCategories);
        }
        expenseCategories.fireTableRowsInserted(expenseCategories.getRowCount(), expenseCategories.getRowCount());
        return expensecategory;
    }

    public Expenses getLoadedExpenses()
    {
        loadedExpenses.clear();
        for(int i = 0; i < expenseCategories.getRowCount(); i++)
        {
            ExpenseCategory expensecategory = (ExpenseCategory)expenseCategories.getObject(i);
            if(expensecategory.view != null)
            {
                Expenses expenses = expensecategory.view.getExpenses();
                for(int k = 0; k < expenses.getRowCount(); k++)
                {
                    loadedExpenses.load(expenses.getObject(k));
                }

            }
        }
        return loadedExpenses;
    }
    
    public Expenses getLoadedImport() {
    	Expenses importExpenses = new Expenses();
        for(int j = 0; j < importCategories.getRowCount(); j++)
        {
            ExpenseCategory expensecategory1 = (ExpenseCategory)importCategories.getObject(j);
            if(expensecategory1.view != null)
            {
                Expenses expenses1 = expensecategory1.view.getExpenses();
                for(int l = 0; l < expenses1.getRowCount(); l++)
                {
                	importExpenses.load(expenses1.getObject(l));
                }

            }
        }

        return importExpenses;
    }

    public static void main(String args[])
    {
        (new ExpenseManager()).show();
    }
}
