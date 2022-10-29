package com.sf.tool.scsb.helper.redmine;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import lombok.Data;

@Data
public class RedmineLevelSummary {
	private int l1Create = 0;
	private int l2Create = 0;
	private int l1Close = 0;
	private int l2Close = 0;
	private int l1OnGoing = 0;
	private int l2OnGoing = 0;
	
	private boolean isL1(IssueModel m) {
		return StringUtils.equalsAny(m.getPriorityText(), "Immediate", "High");
	}
	
	public void add(IssueModel m) {
		if (isL1(m)) {
			if (DateUtils.truncate(new Date(), Calendar.DATE).compareTo(DateUtils.truncate(m.getCreateOn(), Calendar.DATE)) == 0) {
				l1Create++;
			}
			if (StringUtils.equals(m.getStatusName(), "Closed")) {
				l1Close++;
			} else {
				l1OnGoing++;
			}
		} else {
			if (DateUtils.truncate(new Date(), Calendar.DATE).compareTo(DateUtils.truncate(m.getCreateOn(), Calendar.DATE)) == 0) {
				l2Create++;
			}
			if (StringUtils.equals(m.getStatusName(), "Closed")) {
				l2Close++;
			} else {
				l2OnGoing++;
			}
		}
	}
}
