package com.sf.tool.scsb.helper.redmine;

import java.util.Iterator;

import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.ResultsWrapper;

public class PageableIssueList implements Iterator<IssueModel>{
	
	private RedmineManager mgr;
	private int total = 0;
	private Iterator<Issue> result;
	private int idx = 0;
	private String projectId;
	private RequestParam[] params;

	public PageableIssueList(RedmineManager mgr, String projectId) {
		this.mgr = mgr;
		this.projectId = projectId;
		
		getNext();
	}
	
	public PageableIssueList(RedmineManager mgr, String projectId, RequestParam... params) {
		this.mgr = mgr;
		this.projectId = projectId;
		this.params = params;
		getNext();
	}
	
	
	private void getNext() {
		ResultsWrapper<Issue> wrapper;
		try {
			
			Params params = new Params()
				.add("project_id", projectId)
				.add("sort", "id")
				.add("offset", String.valueOf(idx));
			
			if (this.params != null) {
				for (RequestParam p : this.params) {
					params.add(p.getName(), p.getValue());
				}
			}
			
			wrapper = mgr.getIssueManager().getIssues(params);
			this.total = wrapper.getTotalFoundOnServer();
			this.result = wrapper.getResults().iterator();
		} catch (RedmineException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean hasNext() {
		if (this.result.hasNext()) {
			return true;
		} else if (total > idx) {
			getNext();
			return this.result.hasNext();
		} else {
			return false;
		}
	}

	@Override
	public IssueModel next() {
		idx++;
		return new IssueModel(this.result.next());
	}

	
	
}
