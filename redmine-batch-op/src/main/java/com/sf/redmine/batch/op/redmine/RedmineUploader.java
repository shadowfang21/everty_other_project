package com.sf.redmine.batch.op.redmine;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.apache.http.entity.ContentType;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Attachment;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineUploader {
	
//	private static final String uri = "http://161.202.91.233:8300/";
	private static final String uri = "http://10.10.8.151:8200/";
	private static final String apiAccessKey = "4a23c3474c02f6cb0c3961022462cfaec505b216";
	
	private RedmineManager mgr;
	
	private RedmineUploader() {
		mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
	}
	
	public static final RedmineUploader getInstance() {
		return new RedmineUploader();
	}
	
	public boolean check(int ir, String fileName) {
		Issue issue;
		try {
			issue = mgr.getIssueManager().getIssueById(ir, Include.attachments);
			return issue != null && issue.getAttachments().stream()
					.map(Attachment::getFileName)
					.noneMatch(Predicate.isEqual(fileName));
		} catch (RedmineException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void uploadAttachment(int ir, File file) throws RedmineException, IOException {
		mgr.getAttachmentManager().addAttachmentToIssue(ir, file, 
		        ContentType.APPLICATION_OCTET_STREAM.getMimeType());
	}
	
}
