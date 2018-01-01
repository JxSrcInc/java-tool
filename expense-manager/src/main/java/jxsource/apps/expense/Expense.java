package jxsource.apps.expense;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxsource.apps.expense.util.CSVFileParser;

public class Expense {

	public Date date;
	public String description;
	public String merchant;
	public float amount;
	public String attributes;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy");
	private static NumberFormat amountFormat = NumberFormat
			.getCurrencyInstance();
	private static NumberFormat inputAmountFormat = NumberFormat.getInstance();
	private static NumberFormat exportAmountFormat = NumberFormat
			.getCurrencyInstance();

	private float parseAmount(String s) throws Exception {
		try {
			return inputAmountFormat.parse(s).floatValue();
		} catch (Exception e) {
		}
		return amountFormat.parse(s).floatValue();
	}

	public Expense() {
		description = "";
		merchant = "";
		attributes = "";
		amountFormat.setGroupingUsed(true);
		inputAmountFormat.setGroupingUsed(true);
		exportAmountFormat.setGroupingUsed(false);
	}

	public String getDate() {
		return dateFormat.format(date);
	}

	public boolean isDuplicate() {
		return (attributes != null && attributes.toUpperCase().indexOf("D") != -1);
	}

	public Expense set(Date date, String description, String merchant,
			float amount) {
		this.date = date;
		this.description = description;
		this.merchant = merchant;
		this.amount = amount;
		return this;
	}

	public Expense set(String s, String s1, String s2) throws Exception {
		if (s != null && s.trim().length() > 0) {
			date = dateFormat.parse(s);
		}
		description = s1;
		amount = parseAmount(s2);// amountFormat.parse(s2).floatValue();
		return this;
	}

	public Expense set(String s, String s1, String s2, String s3)
			throws Exception {
		if (s != null && s.trim().length() > 0) {
			date = dateFormat.parse(s);
		}
		description = s1;
		merchant = s2;
		amount = parseAmount(s3);// amountFormat.parse(s3).floatValue();
		return this;
	}

	public Expense set(String s, String s1, String s2, String s3, String s4)
			throws Exception {
		if (s != null && s.trim().length() > 0) {
			date = dateFormat.parse(s);
		}
		description = s1;
		merchant = s2;
		amount = parseAmount(s3);// amountFormat.parse(s3).floatValue();
		attributes = s4;
		return this;
	}

	public String toString() {
		return (date != null ? dateFormat.format(date) : "") + ",\""
				+ (description != null ? description : "") + "\",\""
				+ (merchant != null ? merchant : "") + "\","
				+ exportAmountFormat.format(amount) + "," + attributes;
	}

	public Expense set(String s) throws Exception {
		String as[] = CSVFileParser.parse(s);
		if (as.length == 3) {
			return set(as[0], as[1], as[2]);
		}
		if (as.length == 4) {
			return set(as[0], as[1], as[2], as[3]);
		}
		if (as.length == 5) {
			return set(as[0], as[1], as[2], as[3], as[4]);
		} else {
			throw new Exception("Invalid field number.");
		}
	}

	/*
	 * public boolean equals(Object obj) { return
	 * toString().equals(obj.toString()); }
	 */
	public static void main(String args[]) {
		try {
			System.out
					.println((new Expense())
							.set(",\"Mortgage Interest 1/1-9/2/05\",\"Suntrust Mortgage\",($3308.02),"));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + toString().hashCode();
/*		result = prime * result + (int)(round(amount,2)*2);
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((merchant == null) ? 0 : merchant.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
				*/
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expense other = (Expense) obj;
		return toString().equals(other.toString());
/*		float f1 = round(amount, 2);
		float f2 = round(other.amount,2);
		if (f1 != f2)
//		if (f1 != f2)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (merchant == null) {
			if (other.merchant != null)
				return false;
		} else if (!merchant.equals(other.merchant))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
*/
	}

}
