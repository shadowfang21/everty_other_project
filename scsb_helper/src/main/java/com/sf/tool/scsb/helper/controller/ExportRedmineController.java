package com.sf.tool.scsb.helper.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.ContentType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.tool.scsb.helper.redmine.RedmineSummaryExporter;
import com.taskadapter.redmineapi.RedmineException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/redmine")
public class ExportRedmineController {
	
	@Autowired
	private RedmineSummaryExporter redmineSummaryExporter;
	
	@GetMapping("/export/{projectId}")
	public void export(@PathVariable String projectId, HttpServletResponse response) throws IOException, InvalidFormatException, RedmineException {
		
		log.info("start export project {}", projectId);
		
		LocalDate date = LocalDate.now();
		
		String filename = "SCSB eLoan - Redmine統計_" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
		response.setContentType(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
		response.setHeader("Content-disposition", "attachment; filename=" + filename);
		redmineSummaryExporter.exportSummary(projectId, date, response.getOutputStream());
	}
	
}
