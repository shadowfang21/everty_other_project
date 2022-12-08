package com.sf.tool.dto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

public class Column {
    private String columnName;
    private String dataType;
    private String comment;
    private String javaColumnName;
    private String javaDataType;
    private Integer dataLength;
    private Integer dataPrecision;
    private Integer dataScale;

    public boolean isAudit() {
        return (isCreatedUser() || isUpdatedTime() || isUpdatedUser() || isCreateTime());
    }

    public boolean isDelFlag() {
        return this.columnName.equals("DEL_FLAG");
    }

    public boolean isCreateTime() {
        return this.columnName.equals("INSERT_TIMESTAMP");
    }

    public boolean isUpdatedTime() {
        return this.columnName.equals("UPDATE_TIMESTAMP");
    }

    public boolean isUpdatedUser() {
        return this.columnName.startsWith("UPDATED_BY");
    }

    public boolean isCreatedUser() {
        return this.columnName.startsWith("CREATED_BY");
    }

    public boolean isPk() {
        return "LIST_ID".equals(this.columnName);
    }

    public String declareColumn() {
        return String.format("private %s %s;", new Object[] { this.javaDataType, this.javaColumnName });
    }

    public String getColumnGetter() {
        return "get" + StringUtils.capitalize(this.javaColumnName);
    }

    public String getColumnSetter() {
        return "set" + StringUtils.capitalize(this.javaColumnName);
    }

    public String toSetter() {
        StringBuffer sb = new StringBuffer();

        return sb.toString();
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
        this.javaColumnName = CaseUtils.toCamelCase(columnName, false, new char[] { '_' });
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;

        if (isPk() || this.columnName.endsWith("LIST_ID")) {
            this.javaDataType = "Long";
        } else if (StringUtils.equalsAnyIgnoreCase(dataType, new CharSequence[] { "nvarchar", "ntext", "nchar",
                "varchar", "nvarchar2", "varchar2", "blob", "clob","char" })) {
            this.javaDataType = "String";
        } else if (StringUtils.equalsAnyIgnoreCase(dataType, new CharSequence[] { "bigint", "int" })) {
            this.javaDataType = "Long";
        } else if (StringUtils.equalsAnyIgnoreCase(dataType,
                new CharSequence[] { "decimal", "float", "numeric", "number" })) {
            if (this.dataScale.intValue() > 0) {
                this.javaDataType = "java.math.BigDecimal";
            } else if (this.dataPrecision.intValue() <= 4) {
                this.javaDataType = "Integer";
            } else {
                this.javaDataType = "Long";
            }
        } else if (StringUtils.equalsAnyIgnoreCase(dataType, new CharSequence[] { "bit", "smallint", "tinyint" })) {
            this.javaDataType = "Integer";
        } else if (StringUtils.equalsAnyIgnoreCase(dataType,
                new CharSequence[] { "TIMESTAMP(6)", "datetime", "timestamp" })) {
            this.javaDataType = "java.time.LocalDateTime";
        } else if (StringUtils.equalsAnyIgnoreCase(dataType, new CharSequence[] { "date" })) {
            this.javaDataType = "java.time.LocalDate";
        } else if (StringUtils.equalsAnyIgnoreCase(dataType, new CharSequence[] { "TIME" })) {
            this.javaDataType = "java.time.LocalTime";
        } else {
            this.javaDataType = dataType;
        }
    }

    public String getJavaColumnName() {
        return this.javaColumnName;
    }

    public String getJavaDataType() {
        return this.javaDataType;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getDataLength() {
        return this.dataLength;
    }

    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }

    public Integer getDataPrecision() {
        return this.dataPrecision;
    }

    public void setDataPrecision(Integer dataPrecision) {
        this.dataPrecision = dataPrecision;
    }

    public Integer getDataScale() {
        return this.dataScale;
    }

    public void setDataScale(Integer dataScale) {
        this.dataScale = dataScale;
    }
}