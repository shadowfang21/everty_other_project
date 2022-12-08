package com.sf.tool.mapping;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class CodeListMapping {
    private String codeId;
    private String codeDescription;
    private String codeListValue;

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getCodeId() {
        return this.codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCodeListValue() {
        return this.codeListValue;
    }

    public void setCodeListValue(String codeListValue) {
        this.codeListValue = codeListValue;
    }

    public String getCodeDescription() {
        return this.codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }
}