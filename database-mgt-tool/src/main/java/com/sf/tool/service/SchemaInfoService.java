package com.sf.tool.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sf.tool.dao.SchemaInfoDao;
import com.sf.tool.dto.Column;
import com.sf.tool.dto.Table;
import com.sf.tool.dto.TableColumn;

@Service
public class SchemaInfoService {
    @Autowired
    private SchemaInfoDao schemaInfoDao;

    public List<Table> getTableList(String tablePattern) {
        List<TableColumn> tableColumnList = this.schemaInfoDao.getTableInfo(tablePattern);

        return getTableList(tableColumnList);
    }

    private List<Table> getTableList(List<TableColumn> tableColumnList) {
        Map<String, List<TableColumn>> collect = (Map<String, List<TableColumn>>) tableColumnList.stream()
                .filter(p -> !p.getTableName().matches("\\D+\\d+$"))
                .collect(Collectors.groupingBy(TableColumn::getTableName));

        return (List<Table>) collect.entrySet().stream().map(c -> {
            TableColumn tableColumn = ((List<TableColumn>) c.getValue()).get(0);

            Table t = new Table();
            t.setTableCatalog(tableColumn.getTableCatalog());
            t.setTableName(tableColumn.getTableName());
            t.setTableSchema(tableColumn.getTableSchema());
            t.setTableComments(tableColumn.getTableComments());
            t.setColumns(c.getValue().stream()
                    .map(s -> {
                        Column col = new Column();
                        col.setColumnName(s.getColumnName());
                        col.setComment(s.getComments());
                        col.setDataLength(s.getDataLength());
                        col.setDataPrecision(s.getDataPrecision());
                        col.setDataScale(s.getDataScale());
                        col.setDataType(s.getDataType());
                        return col;
                    })
                    .collect(Collectors.toList()));

            return t;
        }).collect(Collectors.toList());
    }
}