package jxsource.apps.expense.xlsloader;

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.opc.OPCPackage;

import jxsource.util.swing.objectview.ObjectTableModel;
import jxsource.apps.expense.*;
import jxsource.apps.expense.util.*;

public class Home_13300_ExpenseLoader implements FileLoader{
	
    public void load(String filename, ObjectTableModel objecttablemodel) throws Exception
    {
		InputStream in = new FileInputStream(filename);
		OPCPackage opcPackage = OPCPackage.open(in);
		XSSFWorkbook book = new XSSFWorkbook(opcPackage);
		XSSFSheet sheet = book.getSheetAt(0);
		for(int i=0; i<sheet.getLastRowNum()+1; i++)
		{
			XSSFRow row = sheet.getRow(i);
			if(row == null)
				continue;
			Expense expense = new Expense();
			boolean updated = false;
			for(int k=0; k<row.getLastCellNum(); k++)
			{
				XSSFCell cell = row.getCell((short)k);
				switch(k)
				{
				case 0:
					// if the first column is not date, skip
					if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
					{
						expense.date = cell.getDateCellValue();
//						updated = true;
					}
					break;
				case 1:
					if(cell != null && cell.toString().trim().length() > 0)
					{
						expense.merchant = cell.toString().trim();
//						updated = true;
					}
					break;
				case 2:
					if(cell != null && cell.toString().trim().length() > 0)
					{
						expense.description = cell.toString().trim();
//						updated = true;
					}
				case 3:
					if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
							&& cell.getNumericCellValue() != 0.0)
					{					
						expense.amount = -(float)cell.getNumericCellValue();
						updated = true;
					}
				case 4:
					if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
							&& cell.getNumericCellValue() != 0.0)
					{					
						expense.amount = -(float)cell.getNumericCellValue();
						updated = true;
					}
				}
			}
			if(updated)
			{
				System.out.println(expense);
				if(objecttablemodel != null)
				objecttablemodel.load(expense);
			}
			in.close();
		}
    }
    	
}
