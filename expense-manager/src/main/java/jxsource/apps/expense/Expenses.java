package jxsource.apps.expense;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import jxsource.util.swing.objectview.ObjectTableModel;

// Referenced classes of package jxsource.apps.assetmanagement.rental:
//            Expense, ExpenseComparator

public class Expenses extends ObjectTableModel
{

    double total;
    Vector invalidLines;
    String AmountColumnName;
    String fieldsToDisplay[] = {
        "attributes", "date", "description", "merchant", "amount"
    };
    String fieldsName[] = {
        "", "date", "description", "merchant", "amount"
    };
	ExpensesComparator comparator = new ExpensesComparator(1);

    @Override
	public void _sort(int column) {
    	comparator.setColumn(column);
		Object[] values = objects.toArray();
		Arrays.sort(values,comparator);
		objects.clear();
		for(int i=0; i<values.length; i++)
		{	objects.add(values[i]);
		}
	}

	Set propertyChangeListener;

    public Expenses()
    {
        super(jxsource.apps.expense.Expense.class);
        invalidLines = new Vector();
        AmountColumnName = "amount";
        propertyChangeListener = new HashSet();
        setIndexOfField(fieldsToDisplay);
    }

    public String getColumnName(int i)
    {
        return fieldsName[i];
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

    public boolean isDuplicate(Expense pExpense) {
		for(Iterator i=objects.iterator(); i.hasNext();)
		{	Expense expense = (Expense)i.next();
			if(expense.date.equals(pExpense.date) &&
				expense.amount == pExpense.amount)
				return true;
		}
		return false;
	}
    
    public Expense getMatch(Expense pExpense) {
		for(Iterator i=objects.iterator(); i.hasNext();)
		{	Expense expense = (Expense)i.next();
			if(expense.equals(pExpense)) {
				return expense;
			}
		}
		return null;
	}
    public Expense getMerchant(String merchant) {
		for(Iterator i=objects.iterator(); i.hasNext();)
		{	Expense expense = (Expense)i.next();
			if(expense.merchant.equals(merchant)) {
				return expense;
			}
		}
		return null;
	}

    public void load(Object obj)
    {
        super.load(obj);
        calculateTotal();
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

    public Object remove(int i)
    {
        Object obj = super.remove(i);
        calculateTotal();
        return obj;
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

    public int load(String s)
        throws IOException
    {
        return load(((InputStream) (new FileInputStream(s))));
    }

    public void setValueAt(Object obj, int i, int j)
    {
        super.setValueAt(obj, i, j);
        if(getColumnName(j).equals(AmountColumnName))
        {
            calculateTotal();
        }
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
        	Expense expense = (Expense)getObject(j);
        	if(!expense.isDuplicate()) {
        		total += expense.amount;
        	}
        }

        if(total != d)
        {
            firePropertyChangeListener(new PropertyChangeEvent(this, "Total", new Double(d), new Double(total)));
        }
        return total;
    }

    public int load(InputStream inputstream)
        throws IOException
    {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
        String s = "";
        invalidLines.clear();
        int i = 0;
        while((s = bufferedreader.readLine()) != null) 
        {
            try
            {
                addExpense(s);
                i++;
            }
            catch(Exception exception)
            {
                invalidLines.add(s);
            }
        }
        calculateTotal();
        return i;
    }

    public void addExpense(String s)
        throws Exception
    {
        load((new Expense()).set(s));
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
            String s = ((Expense)getObject(k)).toString();
            bufferedwriter.write(s, 0, s.length());
            bufferedwriter.newLine();
            bufferedwriter.flush();
            i++;
        }

        bufferedwriter.close();
        return i;
    }

    public int exportToCsv(String s)
    throws IOException
{
    return exportToCsv(((OutputStream) (new FileOutputStream(s))));
}

public int exportToCsv(OutputStream outputstream)
    throws IOException
{
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputstream));
    int i = 0;
    int j = getRowCount();
    for(int k = 0; k < j; k++)
    {
    	Expense exp = (Expense)getObject(k);
    	if(exp.attributes.toUpperCase().indexOf("D") != -1) {
    		continue;
    	}
		float revenue = exp.amount;
		float expense = 0;
		if(exp.amount < 0) {
			expense = -exp.amount;
			revenue = 0;
		}
		out.write(exp.getDate()+","+expense+","+revenue+","+exp.merchant+","+exp.description);
		out.newLine();
		out.flush();
        i++;
    }

    out.close();
    return i;
}

    public Vector getInvalidLines()
    {
        return invalidLines;
    }

    public void sortAllColumns()
    {
        Object aobj[] = objects.toArray();
        Arrays.sort(aobj, new ExpenseComparator());
        objects.clear();
        for(int i = 0; i < aobj.length; i++)
        {
            objects.add(aobj[i]);
        }

    }

    @Override
	public Expense getObject(int i) {
		// TODO Auto-generated method stub
		return (Expense)super.getObject(i);
	}

	public static void main(String args[])
    {
        try
        {
            Expenses expenses = new Expenses();
            System.out.println("load " + expenses.load(args[0]));
            System.out.println("total = " + expenses.calculateTotal());
            System.out.println("export " + expenses.export(System.out));
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
