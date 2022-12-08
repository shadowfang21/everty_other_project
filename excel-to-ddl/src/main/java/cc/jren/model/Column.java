package cc.jren.model;

import lombok.Data;

@Data
public class Column {
	
	private String columnName;
	
	private String dateType;
	
	private String comment;
	
	private String type;
	
	private String typeDesc;
	
	private String notNull;
	
	private Integer length;
	
	private Integer decimal;
	
	private String logicNotNull;
}
