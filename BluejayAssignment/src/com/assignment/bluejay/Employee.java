package com.assignment.bluejay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.regex.ParseException;

public class Employee {

	public static void main(String[] args) {
		
		// Give path of the Excel file stored
		String filePath = "D:\\BluejayAssignment\\Assignment_Timecard.xlsx";
		EmployeeExcelFile(filePath, 7);
		
	}
	
	// Method to get formatted value of cells as a string
	private static String getFormattedCellValue(Cell cell) {
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(cell);
    }

	//@SuppressWarnings("null")
	public static void EmployeeExcelFile(String filePath, int consecutiveDaysLimit) {

		try {
			FileInputStream file = new FileInputStream(new File(filePath));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			Set<String> consecutivePrint = new HashSet<>();
			Set<String> shortBreakPrint = new HashSet<>();
			Set<String> longShiftPrint = new HashSet<>();
			Map<String,String> employeeBreaks = new HashMap<>();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			
			System.out.println("Employees who has worked for 7 consecutive days\n");
			String prevEmpName = "";
			int consecutiveDays = 0;
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				String employeeName = row.getCell(7).getStringCellValue();
				String positionId = row.getCell(0).getStringCellValue();
				
				if(consecutivePrint.contains(employeeName)) {
					continue;
				}
				if(employeeName.equals(prevEmpName)) {
					consecutiveDays++;
				}
				else {
					consecutiveDays = 1;
				}
				
				if(consecutiveDays >= consecutiveDaysLimit) {
					System.out.println("Employee : "+employeeName+", Position : "+positionId);
					consecutivePrint.add(employeeName);
				}
				
				prevEmpName = employeeName;
			}
			
			System.out.println("\n");
			System.out.println("Employees who have less than 10 hours of time between shifts but greater than 1 hour\n");
			
			// reset the iterator
			rowIterator = sheet.iterator();
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				String employeeName = row.getCell(7).getStringCellValue();
				String positionId = row.getCell(0).getStringCellValue();
				
				if(shortBreakPrint.contains(employeeName)) {
					continue;
				}
				
				Cell timeInCell = row.getCell(2);
				Cell timeOutCell = row.getCell(3);
				
				if(timeInCell != null && timeOutCell != null && !timeInCell.toString().equals("Time") && !timeInCell.toString().isEmpty()) {
					
					String timeIn = getFormattedCellValue(timeInCell);
					String timeOut = getFormattedCellValue(timeOutCell);
					
					try {
						Date timeInDate = dateFormat.parse(timeIn);
						Date timeOutDate = dateFormat.parse(timeOut);
						long timeDiff = Math.abs((timeOutDate.getTime() - timeInDate.getTime()))/3600000;
						
						if(timeDiff > 1 && timeDiff < 10) {
							System.out.println("Employee Name : "+employeeName+", Position : "+positionId);
							shortBreakPrint.add(employeeName);
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				//employeeBreaks.put(employeeName, row.getCell(3).getStringCellValue());
			}
			
			System.out.println("\n");
			System.out.println("Employees who has worked for more than 14 hrs in a single shift\n");
			
			rowIterator = sheet.iterator();
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				
				String employeeName = row.getCell(7).getStringCellValue();
				String positionId = row.getCell(0).getStringCellValue();
				
//				if(longShiftPrint.contains(employeeName)) {
//					continue;
//				}
				
				Cell timecardHoursCell = row.getCell(4);
				
				if(!timecardHoursCell.toString().equals("Timecard Hours (as Time)") && timecardHoursCell != null) {
					
						double timecardHours = convertToTotalHours(timecardHoursCell.toString());
						
						if(timecardHours > 14) {
							System.out.println("Employee : "+employeeName+", Position : "+positionId);
							longShiftPrint.add(employeeName);
						}
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found : "+filePath);
		}
		catch(IOException e) {
			System.out.println("An error occured while reading the file");
		}
		catch(ParseException e) {
			System.out.println("An error occured while parsing the data");
		}
		catch(Exception e) {
			System.out.println("An error occured : "+e.getMessage());
		}
	}
	
	private static double convertToTotalHours(String timeString) {
        String[] timeParts = timeString.split(":");
        double totalHours = 0;
        if (timeParts.length == 2) {
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);

            // Calculate total hours
            totalHours = hours + (double) minutes / 60;
            
        }
        return totalHours;
	}

}
