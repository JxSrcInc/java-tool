package jxsource.apps.expense;

import java.text.NumberFormat;

// Referenced classes of package jxsource.apps.assetmanagement.rental:
//            ExpensesView

public class ExpenseCategory
{

    public String category;
    public double amount;
    public ExpensesView view;

    public ExpenseCategory(String s)
    {
        category = s;
        amount = 0.0D;
    }

    public ExpenseCategory(String s, double d, ExpensesView expensesview)
    {
        category = s;
        amount = d;
        view = expensesview;
    }

    public String toString()
    {
        return category + "," + NumberFormat.getCurrencyInstance().format(amount);
    }

    public boolean equals(Object obj)
    {
        return toString().equals(obj.toString());
    }
}
