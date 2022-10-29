package com.sf.redmine.batch.op.redmine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.ContentType;

import com.google.gson.Gson;
import com.sf.redmine.batch.op.TransferRedmineIssueApplication.IssueModel;
import com.sf.redmine.batch.op.TransferRedmineIssueApplication.LocalFileHandler;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class OuterRedmineOperator {
	
	private static final String uri = "http://161.202.91.233:8300/";
	private static final String apiAccessKey = "4a23c3474c02f6cb0c3961022462cfaec505b216";
	
	private LocalFileHandler fileHandler;
	
	public void closeIssue(String... refIssues) throws RedmineException, IOException {
		RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
		
		List<Issue> dataList = Arrays.stream(refIssues)
			.flatMap(ir -> {
				try {
					return mgr.getIssueManager().getIssues(new Params()
							.add("project_id", "33")
							.add("issue_id", ir)
//							.add("cf_6", ir)
							)
							.getResults().stream()
							.peek(issue -> log.info("{}, issue {}, {}, {}, {}", ir, issue.getId(), issue.getAssigneeName(), issue.getStatusName(), issue.getSubject()));
				} catch (RedmineException e) {
					throw new RuntimeException(e);
				}
			})
			.collect(Collectors.toList());
		
		if (dataList.isEmpty()) {
			return;
		}
		
		System.out.print("is everthing OK ?[y/n] : ");
		// Enter data using BufferReader
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));
        // Reading data using readLine
        String name = reader.readLine();
        
		if ("y".equalsIgnoreCase(name)) {
			dataList.forEach(issue -> {
				try {
					issue.setStatusId(5);
					issue.setTransport(mgr.getTransport());
					issue.setClosedOn(new Date());
					issue.update();
					log.info("issue {}, {}, {}, {}", issue.getId(), issue.getAuthorName(), issue.getAssigneeName(), issue.getStatusName());
				} catch (RedmineException e) {
					log.error(e.getMessage(), e);
				}
			});
		} else {
			System.out.println("cancel update");
		}
	}
	
	public void exportOuterToFile() throws RedmineException, IOException {
		RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
		
		List<String> c = mgr.getIssueManager().getIssues(new Params()
				.add("project_id", "33"))
				.getResults().stream()
			.map(IssueModel::new)
			.filter(i -> i.getRefIssue() != null && !"0000".equals(i.getRefIssue()))
			.map(i -> {
				i.setSubject(null);
				i.setDescription(null);
				if (i.getStatusId().intValue() == 17) {
					i.setStatusId(15);
				}
				return i;
			})
			.map(IssueModel::toString)
			.peek(log::info)
			.collect(Collectors.toList());
		
		FileUtils.writeLines(fileHandler.getOuterExportFile(), StandardCharsets.UTF_8.name(), c, "\n");
	}
	
	public void importInto(String file) throws RedmineException, IOException {
		
		RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
		
		Set<Integer> assigner = mgr.getProjectManager().getProjectMembers(33).stream()
			.map(m -> m.getUserId())
			.collect(Collectors.toSet());
		
		FileUtils.readLines(new File(file), StandardCharsets.UTF_8).stream()
			.map(s -> new Gson().fromJson(s, IssueModel.class))
//			.filter(c -> c.getId().intValue() == 6461)
			.filter(c -> assigner.contains(c.getAssigneeId()))
			.peek(c -> log.info(c.info()))
			.forEach(i -> {
				try {
					Optional<Issue> opt = mgr.getIssueManager().getIssues(new Params()
							.add("project_id", "33")
							.add("include", "attachments")
							.add("status_id", "*")
							.add("cf_6", String.valueOf(i.getId()))).getResults().stream().findFirst();
					
					if (opt.isPresent()) {
						Issue issue = opt.get();
						issue.setTransport(mgr.getTransport());
						issue.setSubject(i.getSubject() + "_@" + i.getAuthorName() + "_#" + i.getId());
						
						Optional.ofNullable(issue.getCustomFieldById(9))
							.ifPresent(ref -> {
								ref.setValue(String.valueOf(i.getAuthorId()));
							});
						if (issue.getAttachments().stream()
								.map(c -> c.getFileName())
								.noneMatch(name -> FilenameUtils.getExtension(name).equalsIgnoreCase("pdf"))) {
							mgr.getAttachmentManager().addAttachmentToIssue(issue.getId(), fileHandler.getInterExportPdfFile(i.getId()), 
									ContentType.APPLICATION_OCTET_STREAM.getMimeType());
						}
						issue.update();
					} else {
						Issue issue = new Issue(mgr.getTransport(),33);
						issue.setSubject(i.getSubject() + "_@" + i.getAuthorName() + "_#" + i.getId());
						issue.setCreatedOn(i.getCreateOn());
						issue.setAssigneeId(i.getAssigneeId());
						issue.setDescription(i.getDescription());
						issue.setPriorityId(i.getPriorityId());
						issue.setStatusId(i.getStatusId());
						
						Optional.of(new CustomField())
							.map(ref -> {
								ref.setId(6);
								ref.setName("ref_issue");
								ref.setValue(String.valueOf(i.getId()));
								return ref;
							})
							.ifPresent(issue::addCustomField);
						Optional.of(new CustomField())
						.map(ref -> {
							ref.setId(9);
							ref.setName("ref_author");
							ref.setValue(String.valueOf(i.getAuthorId()));
							return ref;
						})
						.ifPresent(issue::addCustomField);
						
						issue.create();
						
//						mgr.getAttachmentManager().addAttachmentToIssue(issue.getId(), fileHandler.getInterExportPdfFile(i.getId()), 
//								ContentType.APPLICATION_OCTET_STREAM.getMimeType());
					}
					
				} catch (RedmineException | IOException e) {
					log.error(e.getMessage(), e);
				}
			});
	}
}
