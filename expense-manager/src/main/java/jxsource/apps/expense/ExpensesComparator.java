package jxsource.apps.expense;

import java.util.Comparator;

public class ExpensesComparator implements Comparator {

	private int column;

	public ExpensesComparator(int column) {
		this.column = column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	public int compare(Object obj, Object obj1) {
		if(obj == null && obj1 == null) {
			return 0;
		} else
		if(obj == null) {
			return -1;
		} else
		if(obj1 == null) {
			return -1;
		}
		switch (column) {
		case 1:
			return ((Expense) obj).date.compareTo(((Expense) obj1).date);
		case 3:
			return ((Expense) obj).merchant
					.compareTo(((Expense) obj1).merchant);
		case 2:
			return ((Expense) obj).description
					.compareTo(((Expense) obj1).description);

		default:
			if ((((Expense) obj).amount - ((Expense) obj1).amount) > 0) {
				return 1;
			} else if ((((Expense) obj).amount - ((Expense) obj1).amount) < 0) {
				return -1;
			} else {
				return 0;
			}
		}
	}

}
