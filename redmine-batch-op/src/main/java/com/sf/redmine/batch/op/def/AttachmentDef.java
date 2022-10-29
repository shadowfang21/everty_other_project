package com.sf.redmine.batch.op.def;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachmentDef {
	private Integer ir;
	private String fileMonth;
	private String fileName;
	private String fileLocation;
	private String expectedMoveTo;
	private String ignoreMoveTo;
	private String before311MoveTo;
}
