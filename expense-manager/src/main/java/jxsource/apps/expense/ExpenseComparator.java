package jxsource.apps.expense;

import java.util.Comparator;
import java.util.Date;

// Referenced classes of package jxsource.apps.assetmanagement.rental:
//            Expense

public class ExpenseComparator
    implements Comparator
{

    public ExpenseComparator()
    {
    }

    public boolean equals(Object obj)
    {
        return obj != null && (obj instanceof ExpenseComparator);
    }

    public int compare(Object obj, Object obj1)
    {
        int i = 0;
        if(obj == null && obj1 == null)
        {
            i = 0;
        } else
        if(obj == null && obj1 != null)
        {
            i = 1;
        } else
        if(obj != null && obj1 == null)
        {
            i = -1;
        } else
        {
            Expense expense = (Expense)obj;
            Expense expense1 = (Expense)obj1;
            if(expense.date == null || expense1.date == null)
            {
                if(expense.date == null && expense1.date == null)
                {
                    i = 0;
                } else
                if(expense.date == null)
                {
                    i = -1;
                } else
                if(expense1.date == null)
                {
                    i = 1;
                }
            } else
            if(!expense.date.equals(expense1.date))
            {
                i = expense.date.compareTo(expense1.date);
            } else
            if(!expense.description.equals(expense1.description))
            {
                i = expense.description.compareTo(expense1.description);
            } else
            if(!expense.merchant.equals(expense1.merchant))
            {
                i = expense.merchant.compareTo(expense1.merchant);
            } else
            if(expense.amount != expense1.amount)
            {
                if(expense.amount > expense1.amount)
                {
                    i = 1;
                } else
                {
                    i = -1;
                }
            } else
            {
                i = 0;
            }
        }
        return i;
    }
}
