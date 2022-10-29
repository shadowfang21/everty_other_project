package com.sf.tool.scsb.helper.redmine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.internal.RequestParam;

@Component
public class RedmineOperator {
	
	@Value("${app.redmine.url}")
	private String uri;
	
	@Value("${app.redmine.apiAccessKey}")
	private String apiAccessKey;
	
	private RedmineManager mgr;
	
	@PostConstruct
	public void setUp() {
		mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
	}
	
	public String getProjectName(String projectId) {
		try {
			return mgr.getProjectManager().getProjectById(Integer.parseInt(projectId)).getName();
		} catch (NumberFormatException | RedmineException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Iterator<IssueModel> exportAllProductIssue(String projectId) throws RedmineException {
		return new PageableIssueList(mgr, projectId);
	}
	
	public Iterator<IssueModel> exportAllProductClosedIssue(String projectId, LocalDate date) throws RedmineException {
		return new PageableIssueList(mgr, projectId, 
				new RequestParam("status_id", "closed"),
				new RequestParam("closed_on", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
	}
	

}
