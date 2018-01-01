package jxsource.apps.expense.csvloader;

import java.io.*;
import jxsource.apps.expense.Expense;
import jxsource.apps.expense.Expenses;
import jxsource.apps.expense.util.CSVFileParser;
import jxsource.apps.expense.util.FileLoader;
import jxsource.util.swing.objectview.ObjectTableModel;

public class Rental_2260_ExpenseLoader
    implements FileLoader
{

    public Rental_2260_ExpenseLoader()
    {
    }

    public void load(String s, ObjectTableModel objecttablemodel)
        throws Exception
    {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(s)));
        for(String s1 = null; (s1 = bufferedreader.readLine()) != null;)
        {
            String as[] = CSVFileParser.parse(s1);
            System.out.println(as.length);
            
            //if(as.length >= 3 && (as[1].trim().length() != 0 || as[2].trim().length() != 0) && !as[0].toUpperCase().equals("DATE"))
            {
                if(as[0].trim().length() == 0)
                {
                    as[0] = null;
                }
                if(as[5].trim().length() != 0)
                {
                    objecttablemodel.load((new Expense()).set(as[0], as[2].trim(), as[1].trim(), "-" + as[5].trim()));
                } else
                if(as[3].trim().length() != 0)
                {
                    objecttablemodel.load((new Expense()).set(as[0], as[2].trim(), as[1].trim(), as[3].trim()));
                }
            }

        }

        bufferedreader.close();
    }

    public static void main(String args[])
    {
        try
        {
            Expenses expenses = new Expenses();
            (new Rental_2260_ExpenseLoader()).load(args[0], expenses);
            System.out.println(expenses.getRowCount());
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
