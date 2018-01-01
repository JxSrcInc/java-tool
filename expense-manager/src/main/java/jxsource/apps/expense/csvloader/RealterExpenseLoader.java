package jxsource.apps.expense.csvloader;

import java.io.*;
import jxsource.apps.expense.Expense;
import jxsource.apps.expense.Expenses;
import jxsource.apps.expense.util.CSVFileParser;
import jxsource.apps.expense.util.FileLoader;
import jxsource.util.swing.objectview.ObjectTableModel;

public class RealterExpenseLoader
    implements FileLoader
{

    public RealterExpenseLoader()
    {
    }

    public void load(String s, ObjectTableModel objecttablemodel)
        throws Exception
    {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(s)));
        for(String s1 = null; (s1 = bufferedreader.readLine()) != null;)
        {
            String as[] = CSVFileParser.parse(s1);
            if(as.length == 4)
            { 
            	// please match the file field with expense field
            	// date, description, vendor, amount
            	objecttablemodel.load((new Expense()).set(as[0].trim(), as[1].trim(), as[2].trim(), "-"+as[3].trim()));
            }
        }

        bufferedreader.close();
    }

    public static void main(String args[])
    {
        try
        {
            Expenses expenses = new Expenses();
            (new RealterExpenseLoader()).load(args[0], expenses);
            System.out.println(expenses.getRowCount());
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
