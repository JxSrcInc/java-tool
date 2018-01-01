package jxsource.apps.expense;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import jxsource.apps.expense.util.CurrencyRenderer;
import jxsource.apps.expense.util.DuplicateRenderer;
import jxsource.util.swing.objectview.ObjectTable;

// Referenced classes of package jxsource.apps.assetmanagement.rental:
//            Expenses

public class ExpensesView extends JFrame
{

    Expenses expenses;
    JLabel total;
    String strAmount;
    String strTotal;
    Set propertyChangeListener;

    public ExpensesView()
    {
        strAmount = "amount";
        strTotal = "Total";
        propertyChangeListener = new HashSet();
        expenses = new Expenses();
        init();
    }

    private void init()
    {
        setSize(500, 300);
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        ObjectTable objecttable = new ObjectTable(expenses,TransferHandler.MOVE);
        objecttable.getColumnModel().getColumn(expenses.getIndexOfColumn(strAmount)).setCellRenderer(new CurrencyRenderer());
        objecttable.getColumnModel().getColumn(0).setCellRenderer(new DuplicateRenderer());
        objecttable.getColumnModel().getColumn(0).setMaxWidth(1);
        container.add("Center", new JScrollPane(objecttable));
        JToolBar jtoolbar = new JToolBar();
        jtoolbar.add(new JLabel(strTotal + ": "));
        jtoolbar.add(total = new JLabel());
        JButton bttnUpdate = new JButton("Update");
        bttnUpdate.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
            	expenses.calculateTotal();
            	updateTotal();
            }

        });
        JButton bttnAdd = new JButton("Add");
        bttnAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
            	Expense expense = new Expense();
            	expenses.add(expense);
            	expenses.fireTableRowsInserted(0, 0);
            }

        });
        
        updateTotal();
        JPanel jpanel = new JPanel(new BorderLayout());
        jpanel.add("Center", jtoolbar);
        jpanel.add("East", bttnAdd);
 //       jpanel.add("East", bttnUpdate);
        container.add(jpanel, "South");
        expenses.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent propertychangeevent)
            {
                updateTotal();
            }

        });
    }

    public Expenses getExpenses()
    {
        return expenses;
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

    protected void updateTotal()
    {
        double d = expenses.getTotalExpense();
        if(d < 0.0D)
        {
            total.setForeground(Color.red);
        } else
        {
            total.setForeground(Color.black);
        }
        total.setText(NumberFormat.getCurrencyInstance().format(d));
        firePropertyChangeListener(new PropertyChangeEvent(this, "amount", new Double(d), new Double(d)));
    }

    public ExpensesView(File file)
    {
        strAmount = "amount";
        strTotal = "Total";
        propertyChangeListener = new HashSet();
        expenses = new Expenses();
        try
        {
            expenses.load(file.getPath());
            String s = file.getName();
            setTitle(s.substring(0, s.indexOf(".")));
        }
        catch(IOException ioexception)
        {
            setTitle(ioexception.getMessage());
        }
        init();
    }

    public ExpensesView(Expenses expenses1)
    {
        strAmount = "amount";
        strTotal = "Total";
        propertyChangeListener = new HashSet();
        expenses = expenses1;
        init();
    }

    public static void main(String args[])
    {
        (new ExpensesView(new File(args[0]))).show();
    }
}
