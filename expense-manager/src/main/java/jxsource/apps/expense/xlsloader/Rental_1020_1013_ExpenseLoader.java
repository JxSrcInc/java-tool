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

public class Rental_1020_1013_ExpenseLoader implements FileLoader{
	
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
					if(cell != null)
					{
						if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
						{
							expense.date = cell.getDateCellValue();
							updated = true;
						} else
						if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
						{
							expense.description = cell.getStringCellValue();
						}
							
					}
					break;
				case 1:
					if(cell != null && cell.toString().trim().length() > 0)
					{
						expense.merchant = cell.toString().trim();
						//updated = true;
					}
					break;
/*				case 4:
					if(cell != null)
					{
						if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)//cell.getNumericCellValue() != 0.0)
						{					
							expense.amount = -(float)cell.getNumericCellValue()/2;
							updated = true;
						} else
						if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
						{
							try
							{
								expense.amount = - Float.parseFloat(cell.getStringCellValue())/2;
								updated =  true;
							} catch(Exception e){}
						} else
						if(cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
							System.out.println("--> "+cell.getCellFormula());
						}
					}	
					break;
*/				case 3:
					// revene by person
					if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)//cell.getNumericCellValue() != 0.0)
					{					
						expense.amount = (float)cell.getNumericCellValue()/2;
						updated = true;
					}
						
					break;
				case 4:
					// expense by person
					if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)//cell.getNumericCellValue() != 0.0)
					{
						expense.amount = -(float)cell.getNumericCellValue()/2;
						updated = true;
					}
					break;
				case 5:
					// expense for nancy only
					if(cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)//cell.getNumericCellValue() != 0.0)
					{
						expense.amount = -(float)cell.getNumericCellValue();
						updated = true;
					}
					break;
				case 2:
					if(cell != null && cell.toString().trim().length() > 0)
					{
						expense.description = cell.toString().trim();
						//updated = true;
					}
				}
			}
			if(updated && expense.amount != 0.0)
			{
				System.out.println(expense);
				if(objecttablemodel != null)
				objecttablemodel.load(expense);
			}
			in.close();
		}
    }
    	
}
