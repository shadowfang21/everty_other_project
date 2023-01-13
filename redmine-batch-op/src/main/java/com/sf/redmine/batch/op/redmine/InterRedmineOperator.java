package com.sf.redmine.batch.op.redmine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.sf.redmine.batch.op.TransferRedmineIssueApplication.IssueModel;
import com.sf.redmine.batch.op.TransferRedmineIssueApplication.LocalFileHandler;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.RequestParam;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class InterRedmineOperator {
	
	private static final String uri = "http://10.10.8.151:8200/";
	private static final String apiAccessKey = "4a23c3474c02f6cb0c3961022462cfaec505b216";
	
	private LocalFileHandler fileHandler;
	
	public void closeIssue(String... refIssues) throws RedmineException, IOException {
		RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
		
		List<Issue> dataList = Arrays.stream(refIssues)
			.map(ir -> {
				try {
					return mgr.getIssueManager().getIssueById(Integer.parseInt(ir));
				} catch (NumberFormatException | RedmineException e) {
					throw new RuntimeException(e);
				}
			})
			.peek(issue -> {
				log.info("issue {}, {}, {}, {}, {}", issue.getId(), issue.getAuthorName(), issue.getAssigneeName(), issue.getStatusName(), issue.getSubject());
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
			dataList.stream()
			.filter(issue -> issue.getStatusId().intValue() != 15 && issue.getStatusId().intValue() != 5)
			.map(issue -> {
				if (issue.getStatusId().intValue() == 1) {
					issue.setTransport(mgr.getTransport());
					issue.setStatusId(3);
					try {
						issue.update();
					} catch (RedmineException e) {
						throw new RuntimeException(e);
					}
				}
				return issue;
			})
			.forEach(issue -> {
				try {
					issue.setStatusId(15);
					issue.setTransport(mgr.getTransport());
					issue.setAssigneeId(issue.getAuthorId());
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
	
	public void importOuterStatus(File file) throws RedmineException, IOException {
		
		RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
		
		
		FileUtils.readLines(file, StandardCharsets.UTF_8).stream()
			.map(s -> new Gson().fromJson(s, IssueModel.class))
			.peek(log::info)
			.forEach(i -> {
				try {
					Issue opt = mgr.getIssueManager().getIssueById(Integer.parseInt(i.getRefIssue()));
					opt.setTransport(mgr.getTransport());
					opt.setAssigneeId(i.getAssigneeId());
					opt.setStatusId(i.getStatusId());
					
					Optional.ofNullable(opt.getCustomFieldById(5))
						.ifPresent(ref -> {
							ref.setValue(i.getQuestionType());
						});
					opt.update();
				} catch (RedmineException e) {
					log.error(e.getMessage(), e);
				}
			});
			;
	}
	
	public static Iterator<IssueModel> exportAllProductIssue() throws RedmineException {
		RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
		
		return new PageableIssueList(mgr, "27");
	}
	
	public static Iterator<IssueModel> exportAllProductClosedIssue() throws RedmineException {
		RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
		
		return new PageableIssueList(mgr, "27", 
				new RequestParam("status_id", "closed"),
				new RequestParam("closed_on", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
	}
	
	public String exportInterToFileByProject(LocalDate date) throws RedmineException, IOException {
        String format = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);

//        mgr.getProjectManager().getProjects().stream()
//            .forEach(p -> System.out.println(p.getId() + p.getIdentifier()));
        
        Stream.of("34","35","37")
            .flatMap(projectId -> {
                try {
                    return mgr.getIssueManager().getIssues(new Params()
                            .add("project_id", projectId)
                            .add("cf_5","需求變更")).getResults().stream();
                } catch (RedmineException e) {
                    throw new RuntimeException(e);
                }
            })
            .map(IssueModel::new)
//            .filter(issue -> issue.getStatusId().intValue() != 15 && issue.getStatusId().intValue() != 5)
//            .sorted(Comparator.comparing(IssueModel::getId))
            .peek(i -> {
                exportPdf(mgr, i.getId());
            })
            .peek(s -> log.info(s.info()))
//            .map(IssueModel::toString)
            .collect(Collectors.toList());
//        
//        final String f = fileHandler.getInterExportFile(format);
//        
//        FileUtils.writeLines(new File(f), StandardCharsets.UTF_8.name(), c, "\n");
        
        return "";
    }
	
	public String exportInterToFile(LocalDate date) throws RedmineException, IOException {
		String format = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
		
		List<String> c = mgr.getIssueManager().getIssues(new Params().add("created_on", ">=" + 
				format + "T00:00:00Z")).getResults().stream()
			.map(IssueModel::new)
			.filter(issue -> issue.getStatusId().intValue() != 15 && issue.getStatusId().intValue() != 5)
			.sorted(Comparator.comparing(IssueModel::getId))
			.peek(i -> {
				exportPdf(mgr, i.getId());
			})
			.peek(s -> log.info(s.info()))
			.map(IssueModel::toString)
			.collect(Collectors.toList());
		
		final String f = fileHandler.getInterExportFile(format);
		
		FileUtils.writeLines(new File(f), StandardCharsets.UTF_8.name(), c, "\n");
		
		return f;
	}
	
	private void exportPdf(RedmineManager mgr, int id) {
		File file = fileHandler.getInterExportPdfFile(id);

		if (file.exists()) {
			return;
		}
		
		String uu = uri + "issues/" + id + ".pdf";
		
		try {
			mgr.getTransport().download(uu, k -> {
				try {
					FileUtils.copyInputStreamToFile(k.getStream(), file);
					log.info("export file {}", file.getName());
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
				return "";
			});
		} catch (RedmineException e) {
			log.error(e.getMessage(), e);
		}
	}
}
