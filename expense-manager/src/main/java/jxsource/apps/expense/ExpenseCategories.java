package jxsource.apps.expense;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import jxsource.util.swing.objectview.ObjectTableModel;

// Referenced classes of package jxsource.apps.expense:
//            ExpenseCategory, Expenses, ExpenseFactory, ExpensesView

public class ExpenseCategories extends ObjectTableModel
    implements PropertyChangeListener
{

    double total;
    String dataDir;
    File categoryFile[];
    Vector invalidLines;
    String AmountColumnName;
    String fieldsToDisplay[] = {
        "category", "amount"
    };
    Set propertyChangeListener;
    boolean editable;

    public ExpenseCategories()
    {
        super(jxsource.apps.expense.ExpenseCategory.class);
        invalidLines = new Vector();
        AmountColumnName = "amount";
        propertyChangeListener = new HashSet();
        editable = true;
        setIndexOfField(fieldsToDisplay);
    }

    public void setEditable(boolean flag)
    {
        editable = flag;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        propertyChangeListener.add(propertychangelistener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        propertyChangeListener.remove(propertychangelistener);
    }

    protected void firePropertyChangeListener(PropertyChangeEvent propertychangeevent)
    {
        for(Iterator iterator = propertyChangeListener.iterator(); iterator.hasNext(); ((PropertyChangeListener)iterator.next()).propertyChange(propertychangeevent)) { }
    }

    public boolean add(int i, Object aobj[])
    {
        if(containsAtLeastOne(aobj))
        {
            return false;
        } else
        {
            boolean flag = super.add(i, aobj);
            calculateTotal();
            return flag;
        }
    }

    public boolean add(int i, Object obj)
    {
        if(contains(obj))
        {
            return false;
        } else
        {
            boolean flag = super.add(i, obj);
            calculateTotal();
            return flag;
        }
    }

    public Object add(int i)
    {
        Object obj = super.remove(i);
        calculateTotal();
        return obj;
    }

    public boolean isCellEditable(int i, int j)
    {
        if(j == getIndexOfColumn(fieldsToDisplay[0]))
        {
            return editable;
        } else
        {
            return false;
        }
    }

    public void setValueAt(Object obj, int i, int j)
    {
        if(j == getIndexOfColumn(fieldsToDisplay[0]))
        {
            ExpenseCategory expensecategory = (ExpenseCategory)getObject(i);
            if(expensecategory.view == null)
            {
                if(!"Please specify the name of category".equals((String)obj))
                {
                    expensecategory.view = ExpenseFactory.createExpensesView((String)obj);
                    expensecategory.view.getExpenses().addPropertyChangeListener(this);
                    expensecategory.view.show();
                }
            } else
            if(!expensecategory.view.getTitle().equals(obj))
            {
                expensecategory.view.setTitle((String)obj);
            }
            super.setValueAt(obj, i, j);
        }
    }

    public ExpenseCategory getExpenseCategoryByName(String s)
    {
        for(int i = 0; i < getRowCount(); i++)
        {
            ExpenseCategory expensecategory = (ExpenseCategory)getObject(i);
            if(expensecategory.category.equals(s))
            {
                return expensecategory;
            }
        }

        return null;
    }

    public double getTotalExpense()
    {
        return total;
    }

    public double calculateTotal()
    {
        double d = total;
        total = 0.0D;
        int i = getRowCount();
        for(int j = 0; j < i; j++)
        {
            total += ((ExpenseCategory)getObject(j)).amount;
        }

        if(total != d)
        {
            firePropertyChangeListener(new PropertyChangeEvent(this, "Total", new Double(d), new Double(total)));
        }
        return total;
    }

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
        Expenses expenses = (Expenses)propertychangeevent.getSource();
        int i = getRowIndexByExpenses(expenses);
        ExpenseCategory expensecategory = (ExpenseCategory)getObject(i);
        expensecategory.amount = expenses.getTotalExpense();
        fireTableRowsUpdated(i, i);
        calculateTotal();
    }

    public int getRowIndexByExpenses(Expenses expenses)
    {
        for(int i = 0; i < getRowCount(); i++)
        {
            ExpenseCategory expensecategory = (ExpenseCategory)getObject(i);
            if(expensecategory.view.getExpenses().equals(expenses))
            {
                return i;
            }
        }

        return -1;
    }

    public int load()
        throws IOException
    {
        return load(System.getProperty("ExpenseDataSource"));
    }

    public int load(String s)
        throws IOException
    {
        if(s == null)
        {
            throw new IOException("No expense data directory specified.");
        }
        dataDir = s;
        System.out.println("Data directory: " + dataDir);
        File file = new File(dataDir);
        categoryFile = file.listFiles();
        for(int i = 0; i < categoryFile.length; i++)
        {
            load(new ExpenseCategory(categoryFile[i].getName()));
        }

        return categoryFile.length;
    }

    public int export(String s)
        throws IOException
    {
        return export(((OutputStream) (new FileOutputStream(s))));
    }

    public int export(OutputStream outputstream)
        throws IOException
    {
        BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(outputstream));
        int i = 0;
        int j = getRowCount();
        for(int k = 0; k < j; k++)
        {
            String s = ((ExpenseCategory)getObject(k)).toString();
            bufferedwriter.write(s, 0, s.length());
            bufferedwriter.newLine();
            bufferedwriter.flush();
            i++;
        }

        bufferedwriter.close();
        return i;
    }

    public static void main(String args[])
    {
        try
        {
            ExpenseCategories expensecategories = new ExpenseCategories();
            System.out.println("load " + expensecategories.load(args[0]));
            System.out.println("total = " + expensecategories.calculateTotal());
            System.out.println("export " + expensecategories.export(System.out));
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
