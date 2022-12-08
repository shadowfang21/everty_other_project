package cc.jren.generator;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import cc.jren.enums.DataType;
import cc.jren.model.Column;
import cc.jren.model.Table;
import cc.jren.model.TableSheet;

public class TableSheetToTable {

	public static Table toTable(List<TableSheet> list){
		
		Table table = toTable().apply(list);
		setColumn().accept(table.getColumns());
		
		return table;
	}
	
	private static Consumer<List<Column>> setColumn() {
		return dataList -> {
			dataList.stream()
				.peek(column -> {
					for (DataType dataType : DataType.values()) {
						if (dataType.getChecker().test(column)) {
							column.setDateType(dataType.name());
							
							Pair<Integer, Integer> pair = dataType.getLengthFun().apply(column);
							column.setLength(pair.getLeft());
							column.setDecimal(pair.getRight());
							break;
						}
					}
				})
				.forEach(column -> {
					if (column.getDateType() == null) {
						throw new RuntimeException(column.toString());
					}
				});
		};
	}
	
	private static Function<List<TableSheet>, Table> toTable(){
		return item->{
			TableSheet tableSheet = item.get(0);
			Table table = new Table();
			
			table.setTableName(tableSheet.getTableName());
			table.setTableDesc(tableSheet.getTableDesc());
			table.setColumns(item.stream().map(c->{
				Column column = new Column();
				column.setColumnName(c.getColumnName());
				column.setComment(c.getColumnDesc());
				column.setNotNull(StringUtils.equals(c.getRequired(), "◎") ? "Y" : "N");
				column.setLogicNotNull(StringUtils.equals(c.getRequired2(), "◎") ? "Y" : "N");
				column.setType(c.getType());
				column.setTypeDesc(c.getTypeDesc());
				column.setDecimal(0);
				column.setLength(0);
				return column;
			}).collect(Collectors.toList()));
			
			return table;
		};
	}
	
}
