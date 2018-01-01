package jxsource.apps.expense;


// Referenced classes of package jxsource.apps.assetmanagement.rental:
//            Expense

public class RawExpense extends Expense
{

    private boolean duplicateFlag;

    public RawExpense()
    {
        duplicateFlag = false;
    }

    public boolean getDuplicateFlag()
    {
        return duplicateFlag;
    }

    public void setDuplicateFlag(boolean flag)
    {
        duplicateFlag = flag;
    }
}
