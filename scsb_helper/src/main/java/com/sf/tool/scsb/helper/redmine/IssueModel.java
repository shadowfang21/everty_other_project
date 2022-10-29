package com.sf.tool.scsb.helper.redmine;

import java.util.Date;
import java.util.Optional;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taskadapter.redmineapi.bean.Issue;

import lombok.Data;

@Data
public class IssueModel {
	
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
