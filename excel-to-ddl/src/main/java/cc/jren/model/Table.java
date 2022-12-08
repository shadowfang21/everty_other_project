package cc.jren.model;

import java.util.List;

import lombok.Data;

@Data
public class Table {

	private String tableName;
	
	private String tableDesc;
	
	private List<Column> columns;

}
