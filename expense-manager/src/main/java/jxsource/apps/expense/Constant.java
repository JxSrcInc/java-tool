package jxsource.apps.expense;


public class Constant
{

    public static final String SystemPropertyName_ImportFile = "ImportFile";
    public static final String nameOfCSVFileLoaderClass[] = {
//        "jxsource.apps.expense.csvloader.CsvLoader", 
        "jxsource.apps.expense.xlsloader.ZnnBusinessExpenseXlsLoader", 
//        "jxsource.apps.expense.xlsloader.JxSrcXlsxLoader", 
//        "jxsource.apps.expense.xlsloader.XlsLoader", 
        "jxsource.apps.expense.xlsloader.ExpenseXlsxLoader", 
        "jxsource.apps.expense.xlsloader.Rental_5376_ExpenseLoader", 
        "jxsource.apps.expense.xlsloader.Rental_ZNN_ZQQ_XlsLoader", 
        "jxsource.apps.expense.xlsloader.Home_13300_ExpenseLoader", 
    };
	
    public static String Home;
    static 
    {
    	Home = "c:\\Users\\Public\\Documents\\personal\\tax\\2014\\expense";
        String home = System.getProperty("jxsource.apps.expense.home");
        if(home != null)
        {
        	Home = home;
        }
    }
 
}
