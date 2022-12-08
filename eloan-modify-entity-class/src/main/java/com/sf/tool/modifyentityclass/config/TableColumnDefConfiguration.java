package com.sf.tool.modifyentityclass.config;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.Data;
import lombok.ToString;

public class TableColumnDefConfiguration {
	
	private static final String FILE_PATH = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\info\\20211111_edw_column_modify\\all_tab_cols.xlsx";
	
	private List<TableColumnDef> defList = new LinkedList<>();
	
	public void load() throws IOException {
		try (XSSFWorkbook wb = new XSSFWorkbook(FILE_PATH)) {
			XSSFSheet sheet = wb.getSheetAt(1);
			
			for (int i = 1 ; i < sheet.getPhysicalNumberOfRows() ; i++) {
				XSSFRow row = sheet.getRow(i);
				
				TableColumnDef def = new TableColumnDef();
				def.setTableName(row.getCell(1).toString());
				def.setColumnName(row.getCell(2).toString());
				def.setProdType(row.getCell(5).toString());
				try {
					def.setTestType(row.getCell(7).getStringCellValue());
				} catch (Exception e) {
					def.setTestType(null);
				}
				defList.add(def);
				
				System.out.println(def);
			}
		}
	}
	
	@Data
	@ToString
	public static class TableColumnDef {
		private String tableName;
		private String columnName;
		private String prodType;
		private String testType;
	}
	
}
