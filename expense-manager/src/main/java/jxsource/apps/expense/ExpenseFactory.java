package jxsource.apps.expense;


// Referenced classes of package jxsource.apps.assetmanagement.rental:
//            ExpensesView

public class ExpenseFactory
{

    static int x = 30;
    static int y = 30;
    static int i = 0;

    public ExpenseFactory()
    {
    }

    public static ExpensesView createExpensesView(String s)
    {
        ExpensesView expensesview = new ExpensesView();
        expensesview.setTitle(s);
        expensesview.setLocation(x * i, y * i);
        if(i < 15)
        {
            i++;
        } else
        {
            i = 0;
        }
        return expensesview;
    }

}
