package jxsource.apps.expense.xlsloader;

import org.apache.poi.poifs.filesystem.*;
import java.io.*;
import org.apache.poi.hssf.usermodel.*;

import jxsource.util.swing.objectview.ObjectTableModel;
import jxsource.apps.expense.*;
import jxsource.apps.expense.util.*;
/*
 * 0: date
 * 1: merchant
 * 2: description
 * 3: expense
 * 5: income
 */
public class Rental_5376_ExpenseLoader implements FileLoader{
	
    public void load(String filename, ObjectTableModel objecttablemodel) throws Exception
    {
    	try{
		InputStream in = new FileInputStream(filename);
		POIFSFileSystem fs = new POIFSFileSystem(in);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
//		HSSFSheet sheet = wb.getSheetAt(2);
		HSSFSheet sheet = wb.getSheetAt(0);
		for(int i=0; i<sheet.getLastRowNum()+1; i++)
		{
			if(i==0) continue;
			HSSFRow row = sheet.getRow(i);
			if(row == null)
				continue;
			Expense expense = new Expense();
			boolean updated = false;
			for(int k=0; k<row.getLastCellNum(); k++)
			{
				HSSFCell cell = row.getCell((short)k);
//				System.out.println(cell);
				switch(k)
				{
				case 0:
					if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
					{
						expense.date = cell.getDateCellValue();
						updated = true;
					}
					break;
				case 1:
					if(cell != null && cell.toString().trim().length() > 0)
					{
						expense.merchant = cell.toString().trim();
						updated = true;
					}
					break;
				case 3:
					if(cell != null && cell.getNumericCellValue() != 0.0)
					{					
						expense.amount = -(float)cell.getNumericCellValue();
						updated = true;
					}
					break;
				case 5:
					if(cell != null) 
					{			
						if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC && cell.getNumericCellValue() != 0.0)
						{
							expense.amount = (float)cell.getNumericCellValue();
							updated = true;
						} else
						if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING && cell.toString().trim().length() > 0)
						{
							expense.description = cell.toString().trim();
							updated = true;
						}
					}
					break;
				case 2:
					if(cell != null && cell.toString().trim().length() > 0)
					{
						expense.description = cell.toString().trim();
						updated = true;
					}
				}
			}
			if(updated)
			{
//				System.out.println(expense);
				if(objecttablemodel != null)
				objecttablemodel.load(expense);
			}
			in.close();
		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw e;
    	}
    }
    	
}
