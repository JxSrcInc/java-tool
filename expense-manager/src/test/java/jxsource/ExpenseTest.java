package jxsource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import jxsource.apps.expense.*;
import jxsource.apps.expense.xlsloader.*;

/**
 * Unit test for simple App.
 */
public class ExpenseTest 
    extends TestCase
{
		ExpenseManager em;
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ExpenseTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ExpenseTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {	
    	try
    	{
    		new Rental_1020_1013_ExpenseLoader().load(
    				"c:\\Documents and Settings\\jli\\My Documents\\personal\\business_expense\\2007\\raw\\Expense_2660_Merlin_07.xls", null);
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
/*    	em = new ExpenseManager();
			Runnable r = new Runnable()
			{	public void run()
				{	em.show();
				}
			};
			new Thread(r).start();
			try 
			{	Thread.sleep(1000);
			} catch(Exception e) {}
			System.out.println(em.isVisible());
			while(em.isVisible())
			try 
			{	Thread.sleep(1000*60);
			} catch(Exception e) {}
*/
    }
}
