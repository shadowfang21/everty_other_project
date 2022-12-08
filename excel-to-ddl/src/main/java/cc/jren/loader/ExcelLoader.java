package cc.jren.loader;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cc.jren.model.RptDef;
import cc.jren.model.TableSheet;
import cc.jren.utils.CellUtil;

public class ExcelLoader {
	
	
	
	public static List<String> loadNumber(String filePath) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
		decimalFormat.setMaximumFractionDigits(15);
		
		System.out.println("read file : " + filePath);
		List<String> result = new LinkedList<>();
		try (Workbook workbook = new XSSFWorkbook(new File(filePath));) {

			Sheet sheet = workbook.getSheetAt(0);
			
			AtomicInteger atomicIndex = new AtomicInteger(0);
			sheet.rowIterator().forEachRemaining(row -> {
				if (atomicIndex.getAndIncrement() <= 4) {
					return;
				}
				
				
				result.add(IntStream.range(0, 15)
					.mapToDouble(i -> row.getCell(7 + i).getNumericCellValue())
					.mapToObj(s -> decimalFormat.format(s))
					.collect(Collectors.joining(",")));
				
				
//				RptDef def = new RptDef();
//				def.setRptId(CellUtil.getCellStringValue(row, 0));
//				def.setRptName(CellUtil.getCellStringValue(row, 7));
				
//				result.add(values.stream()
//						.map(String::valueOf)
//						.collect(Collectors.joining(",")));
			});
		} catch (IOException | InvalidFormatException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static List<RptDef> loadRptDef(String filePath) {
		System.out.println("read file : " + filePath);
		List<RptDef> result = new LinkedList<>();
		try (Workbook workbook = new XSSFWorkbook(new File(filePath));) {

			Sheet sheet = workbook.getSheetAt(0);
			
			AtomicInteger atomicIndex = new AtomicInteger(0);
			sheet.rowIterator().forEachRemaining(row -> {
				if (atomicIndex.getAndIncrement() <= 0) {
					return;
				}
				
				RptDef def = new RptDef();
				def.setRptId(CellUtil.getCellStringValue(row, 0));
				def.setRptName(CellUtil.getCellStringValue(row, 7));
				
				result.add(def);
			});
		} catch (IOException | InvalidFormatException e) {
			throw new RuntimeException(e);
		} 
		return result;
	}
	
	public static List<List<TableSheet>> load(String filePath) {
		System.out.println("read file : " + filePath);
		List<List<TableSheet>> result = new LinkedList<>();
		try (Workbook workbook = new XSSFWorkbook(new File(filePath));) {

			workbook.sheetIterator().forEachRemaining(sheet -> {
				
				System.out.println("read sheet : " + filePath);
				
				List<TableSheet> list = new LinkedList<>();
				
				AtomicInteger atomicIndex = new AtomicInteger(0);
				
				String desc = CellUtil.getCellStringValue(sheet.getRow(0), 1);
				String tableName = CellUtil.getCellStringValue(sheet.getRow(0), 3);
				sheet.rowIterator().forEachRemaining(row -> {
					if (atomicIndex.getAndIncrement() <= 1) {
						return;
					}
					
					TableSheet tableSheet = new TableSheet();
					tableSheet.setTableName(StringUtils.defaultString(tableName, sheet.getSheetName()));
					tableSheet.setTableDesc(desc);
					tableSheet.setColumnName(CellUtil.getCellStringValue(row, 0).toUpperCase());
					tableSheet.setColumnDesc(CellUtil.getCellStringValue(row, 1));
					tableSheet.setType(StringUtils.defaultString(CellUtil.getCellStringValue(row, 2),"").toUpperCase());
					tableSheet.setTypeDesc(StringUtils.defaultString(CellUtil.getCellStringValue(row, 3), "").toUpperCase());
					tableSheet.setRequired(CellUtil.getCellStringValue(row, 4));
					tableSheet.setRequired2(CellUtil.getCellStringValue(row, 5));
					
					list.add(tableSheet);
				});
				result.add(list);
			});
		} catch (IOException | InvalidFormatException e) {
			throw new RuntimeException(e);
		} 
		return result;
	}
	
}
