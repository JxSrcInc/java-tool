package jxsource.apps.expense;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;

import jxsource.apps.expense.csvloader.CsvLoader;
public class MarriottVisaCsvConverter {
	Expenses expenses = new Expenses();
	public Expenses  load(String filename) throws Exception {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream(filename)));
		String line = null;
		while((line = in.readLine()) != null) {
			int i = line.indexOf(' ');
			String date = line.substring(0,i).trim();
			int k = date.lastIndexOf('/');
			date = date.substring(0,k+1)+"20"+date.substring(k+1);			
			line = line.substring(i+1).trim();
			i = line.lastIndexOf(' ');
			String merchant = line.substring(0,i).trim();
			String amount = line.substring(i+1).trim();
			i = merchant.indexOf(',');
			String state = merchant.substring(i+1).trim();
			merchant = merchant.substring(0, i).trim();
			i = merchant.lastIndexOf(' ');
			if(i > 0) {
				String number = merchant.substring(i+1);
				Pattern MY_PATTERN = Pattern.compile("[0-9]");
				Matcher m = MY_PATTERN.matcher(number);
				if (m.find()) {
				    merchant = merchant.substring(0,i).trim();
				}

			}
			merchant = WordUtils.capitalizeFully(merchant);
			Expense expense = new Expense().set(date, state, merchant, "-"+amount);
//			System.out.println(expense);
			expenses.add(expense);
		}
		return expenses;
	}

	public void merge(String filename, Expenses expenses) throws Exception {
		new CsvLoader().load(filename, expenses);

	}
	public static void main(String[] args) {
		try{
			String csv = "C:/Users/Public/Documents/personal/tax/2013/expense/data/MarriottDownload.csv";
			MarriottVisaCsvConverter converter = new MarriottVisaCsvConverter();
			Expenses expenses = converter.load(csv);
//            (new CsvLoader()).load("C:/Users/Public/Documents/personal/tax/2013/expense/data/transaction.csv", expenses);
			String outputFile = "C:/Users/Public/Documents/personal/tax/2013/expense/data/MarriottVisa.csv";
			expenses.exportToCsv(outputFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
