package jxsource.apps.expense;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
//import jxsource.util.io.filter.ExtensionFileChooserFilter;
import jxsource.apps.expense.util.FileLoader;
import jxsource.apps.expense.util.FileLoaderFactory;
import jxsource.util.swing.objectview.ObjectTableModel;

// Referenced classes of package jxsource.apps.assetmanagement.rental:
//            Expenses

public class ImportFile
{

    File file;
    JFileChooser fileChooser;
    Component parent;
//    ExtensionFileChooserFilter extFilter = new ExtensionFileChooserFilter(new String[]{"csv","xls"});

    public ImportFile()
    {
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(Constant.Home));
    }

    public boolean load(ObjectTableModel objecttablemodel)
        throws Exception
    {
        return load(System.getProperty("ImportFile"), objecttablemodel);
    }

    public boolean load(String csvFileName, ObjectTableModel objecttablemodel)
        throws Exception
    {
        if(csvFileName != null && (new File(csvFileName)).exists())
        {
        	
            file = new File(csvFileName);
        } else
        {	
        	// select csv file from Constant.DefaultHome directory
        	if(!new File(Constant.Home).exists())
        		System.out.println(Constant.Home+" does not exist.");
        	fileChooser.setCurrentDirectory(new File(Constant.Home));
        	//fileChooser.addChoosableFileFilter(extFilter);
            int i = fileChooser.showOpenDialog(parent);
            if(i == 0 && fileChooser.getSelectedFile() != null)
            {
                file = fileChooser.getSelectedFile();
            } else
            {
                return false;
            }
        }
        FileLoaderFactory.setNameOfFileLoaderClass(Constant.nameOfCSVFileLoaderClass);
        FileLoader fileLoader = FileLoaderFactory.create(file.getName());
        fileLoader.load(file.getPath(), objecttablemodel);
        return true;
    }

    public void setParent(Component component)
    {
        parent = component;
    }

    public File getFile()
    {
        return file;
    }

    public static void main(String args[])
    {
        try
        {
            Expenses expenses = new Expenses();
            (new ImportFile()).load(expenses);
            System.out.println(expenses.getRowCount());
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
