package com.sf.redmine.batch.op;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sf.redmine.batch.op.redmine.InterRedmineOperator;
import com.sf.redmine.batch.op.redmine.OuterRedmineOperator;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

import lombok.Data;

public class TransferRedmineIssueApplication {
	
	public static class LocalFileHandler {
		private static String defaultOutputPath;
		private static final String inter = "\\inter\\";
		private static final String outer = "\\outer\\";
		
		
		public String getInterExportFile(String format) {
			return defaultOutputPath + inter + format;
		}
		public File getInterExportPdfFile(int id) {
			return new File(defaultOutputPath + inter + id + ".pdf");
		}
		public File getOuterExportFile() {
			return new File(defaultOutputPath + outer + "export");
		}
		
		public LocalFileHandler() {
			defaultOutputPath = LocalFileHandler.class.getResource(".").getPath()
            		.replaceAll("classes.*", "") + "/export/";
		}
		
	}
	

	
	public static void main(String[] args) throws RedmineException, IOException {
		LocalFileHandler fileHandler = new LocalFileHandler();
		LocalDate date = LocalDate.of(2022,5,16);
		
		String target = "6455,6449,6448,6446,6445,6444,6443";
		
		
		InterRedmineOperator inter = new InterRedmineOperator(fileHandler);
//		inter.importOuterStatus(fileHandler.getOuterExportFile());
//		inter.exportInterToFile(date);
//		inter.closeIssue(target.split(","));
		
		OuterRedmineOperator outer = new OuterRedmineOperator(fileHandler);
		outer.exportOuterToFile();
//		outer.importInto(fileHandler.getInterExportFile(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
//		outer.closeIssue(target.split(","));
	}
	
	
	@Data
	public static class IssueModel {
		
		private static final Gson infoG = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return f.getName().equals("description");
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				return false;
			}
			
		}).create();
		
		private Integer id;
		private Date createOn;
		private String subject;
		private Integer statusId;
		private Integer priorityId;
		private Integer authorId;
		private String description;
		private Integer assigneeId;
		private String statusName;
		private String assigneeName;
		private String authorName;
		private String priorityText;
		private String refIssue;
		private String refAuthor;
		private Date updatedOn;

		private String questionType;

		private Date closedOn;

		public IssueModel(Issue issue) {
			this.id = issue.getId();
			this.createOn = issue.getCreatedOn();
			this.subject = issue.getSubject();
			this.statusId = issue.getStatusId();
			this.statusName = issue.getStatusName();
			this.priorityId = issue.getPriorityId();
			this.priorityText = issue.getPriorityText();
			this.authorId = issue.getAuthorId();
			this.authorName = issue.getAuthorName();
			this.description = issue.getDescription();
			this.assigneeId = issue.getAssigneeId();
			this.assigneeName = issue.getAssigneeName();
			this.updatedOn = issue.getUpdatedOn();
			this.closedOn = issue.getClosedOn();
			
			this.refIssue = Optional.ofNullable(issue.getCustomFieldByName("ref_issue"))
						.map(s -> s.getValue())
						.orElse(null);
			this.refAuthor = Optional.ofNullable(issue.getCustomFieldByName("ref_author"))
					.map(s -> s.getValue())
					.orElse(null);
			this.questionType = Optional.ofNullable(issue.getCustomFieldById(5)) //問題類別
					.map(s -> s.getValue())
					.orElse(null);
		}
		public String toString() {
			return new Gson().toJson(this);
		}
		public String info() {
			return infoG.toJson(this);
		}
	}
}
