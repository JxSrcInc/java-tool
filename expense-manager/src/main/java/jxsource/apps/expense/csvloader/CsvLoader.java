package jxsource.apps.expense.csvloader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import jxsource.apps.expense.Expense;
import jxsource.apps.expense.Expenses;
import jxsource.apps.expense.util.CSVFileParser;
import jxsource.apps.expense.util.FileLoader;
import jxsource.util.swing.objectview.ObjectTableModel;

public class CsvLoader
    implements FileLoader
{

    public CsvLoader()
    {
    }

    public void load(String s, ObjectTableModel expenses)
        throws Exception
    {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(s)));
        int count = 0;
        for(String s1 = null; (s1 = bufferedreader.readLine()) != null;)
        {
        	if((count++) == 0) {
        		continue;
        	}
            String as[] = CSVFileParser.parse(s1);
            if(as.length == 5)
            { 
            	// please match the file field with expense field
            	// date, description, vendor, amount
            	String date = as[0].trim();
            	String revenue = "-"+as[1].trim();
            	if(as[2] != null && as[2].trim().length() > 0 &&
            			Float.parseFloat(as[2].trim()) != 0) {
            		revenue = as[2].trim();
            	}
            	String toFrom = as[3].trim();
            	String desc = as[4].trim();
            	Expense expense = (new Expense()).set(date, desc, toFrom, revenue);
            	expenses.add(expense);
            }
        }

        bufferedreader.close();
   }

    public static void main(String args[])
    {
        try
        {
            List<Expense> expenses = new ArrayList<Expense>();
            (new CsvLoader()).load("C:/Users/Public/Documents/personal/tax/2013/expense/data/transaction.csv", new Expenses());
         //   System.out.println(expenses);
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
