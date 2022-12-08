package cc.jren.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

public class CellUtil {

	private static DataFormatter formatter = new DataFormatter();
	
	public static String getCellStringValue(Row row, int index) {
		Cell cell = getCell(row, index);
		if(cell.getCellType() == CellType.STRING) {
			return StringUtils.stripToNull(cell.getStringCellValue()).toUpperCase();
		}else if(cell.getCellType() == CellType.NUMERIC) {
			return StringUtils.stripToNull(formatter.formatCellValue(cell)).toUpperCase();
		}
		
		return null;
	}
	
	private  static Cell getCell(Row row, int index) {
		return org.apache.poi.ss.util.CellUtil.getCell(row, index);
	}
}
