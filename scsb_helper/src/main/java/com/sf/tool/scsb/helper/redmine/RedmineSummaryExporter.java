package com.sf.tool.scsb.helper.redmine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.RedmineException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedmineSummaryExporter {
	
	@Autowired
	private RedmineOperator redmineOperator;
	
	private boolean filterIssueModel(IssueModel c) {
		return !StringUtils.equalsAny(c.getQuestionType(), "需求變更", "問題重複", "操作問題");
	}
	
	public OutputStream exportSummary(String projectId, LocalDate date, OutputStream os) throws IOException, InvalidFormatException, RedmineException {
		
		InputStream inputStream = IOUtils.toBufferedInputStream(new ClassPathResource("template.xlsx").getInputStream());
		
		RedmineLevelSummary summary = new RedmineLevelSummary();
		
		redmineOperator.exportAllProductClosedIssue(projectId, date).forEachRemaining(c -> {
			if (filterIssueModel(c)) {
				summary.add(c);
			}
			
		});
		
		writeToExcel(projectId, inputStream, os, summary, redmineOperator.exportAllProductIssue(projectId), date);
		
		log.info("{}", summary);
		
		return os;
	}
	
	private void writeToExcel(String projectId, InputStream is, OutputStream os, RedmineLevelSummary summary, Iterator<IssueModel> iterator, LocalDate date) throws InvalidFormatException {
		
		String projectName = redmineOperator.getProjectName(projectId);
		
		try (Workbook workbook = new XSSFWorkbook(is);
				OutputStream fos = os) {
			
			Font font = workbook.createFont();
			font.setFontName("微軟正黑體");
			font.setFontHeightInPoints((short) 10);
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			
			workbook.setSheetName(0, projectName + "(ALL)");
			workbook.setSheetName(1, projectName);
			
			Sheet all = workbook.getSheetAt(0);
			Sheet forcus = workbook.getSheetAt(1);
			
			iterator.forEachRemaining(c -> {
				addRow(all, style, c);
				if (filterIssueModel(c)) {
					addRow(forcus, style, c);
					summary.add(c);
				}
			});
			
			Sheet summarySheet = workbook.getSheetAt(2);
			Optional.of(summarySheet.getRow(0))
				.ifPresent(r -> {
					r.getCell(0).setCellValue(projectName);
					r.getCell(1).setCellValue(date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
				});
			
			Optional.of(summarySheet.getRow(2))
			.ifPresent(r -> {
				r.getCell(1).setCellValue(summary.getL1Create());
				r.getCell(2).setCellValue(summary.getL2Create());
			});
			Optional.of(summarySheet.getRow(3))
			.ifPresent(r -> {
				r.getCell(1).setCellValue(summary.getL1Close());
				r.getCell(2).setCellValue(summary.getL2Close());
			});
			Optional.of(summarySheet.getRow(4))
			.ifPresent(r -> {
				r.getCell(1).setCellValue(summary.getL1OnGoing());
				r.getCell(2).setCellValue(summary.getL2OnGoing());
			});
			
			workbook.write(fos);
		} catch (EncryptedDocumentException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Cell cell(Cell cell, CellStyle style, String value) {
		cell.setCellStyle(style);
		cell.setCellValue(value);
		return cell;
	}
	
	private void addRow(Sheet sheet, CellStyle style, IssueModel issue) {
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		
		cell(row.createCell(0), style, String.valueOf(issue.getId()));
		cell(row.createCell(1), style, issue.getQuestionType());
		cell(row.createCell(2), style, issue.getPriorityText());
		cell(row.createCell(3), style, issue.getStatusName());
		cell(row.createCell(4), style, issue.getSubject());
		cell(row.createCell(5), style, issue.getAuthorName());
		cell(row.createCell(6), style, issue.getAssigneeName());
		cell(row.createCell(7), style, LocalDate.ofInstant(issue.getCreateOn().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
		cell(row.createCell(8), style, LocalDateTime.ofInstant(issue.getUpdatedOn().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
	}
}
