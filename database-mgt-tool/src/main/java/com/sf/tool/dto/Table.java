package com.sf.tool.dto;

import java.util.List;

import org.apache.commons.text.CaseUtils;

public class Table {
    private String tableCatalog;
    private String tableSchema;
    private String tableName;
    private String tableComments;
    private List<Column> columns;
    
    public String getServiceName() {
        
        if (containAppealFlag()) {
            return "CaseAppealBaseService";
        } else if (isDetail()) {
            return "CaseBaseDetailService";
        } else if (columns.stream()
                .anyMatch(c -> "CASE_SN".equals(c.getColumnName()))) {
            return "CaseBaseService";
        }
        return "DtoRepositoryService";
    }

    public boolean containAppealFlag() {
        return this.columns.stream()
                    .anyMatch(c -> "APPEAL_FLAG".equals(c.getColumnName()));
    }
    
    public boolean isDetail() {
        return this.columns.stream()
                .anyMatch(c -> "MASTER_LIST_ID".equals(c.getColumnName()));
    }
    
    public boolean containAuditInfo() {
        return this.columns.stream()
                .anyMatch(c -> (c.isCreateTime() || c.isCreatedUser() || c.isUpdatedTime() || c.isUpdatedUser()));
    }

    public String toClassName() {
        return CaseUtils.toCamelCase(this.tableName, true, new char[] { '_' });
    }

    public String toCamelCase() {
        return CaseUtils.toCamelCase(this.tableName, false, new char[] { '_' });
    }

    public String getTableCatalog() {
        return this.tableCatalog;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    public String getTableSchema() {
        return this.tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String getTableComments() {
        return this.tableComments;
    }

    public void setTableComments(String tableComments) {
        this.tableComments = tableComments;
    }
}