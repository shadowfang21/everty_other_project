package com.sf.tool.entity;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "CMN_CODE_LIST_DEF")
public class CmnCodeListDefPo {
    @Id
    @Column(name = "CODE_ID")
    private String codeId;
    @Column(name = "CODE_DESCRIPTION")
    private String codeDescription;
    @Column(name = "DEL_FLAG")
    private String delFlag = "N";

    @Column(name = "DATA_LENGTH")
    private Integer dataLength;

    @Column(name = "PARENT_ID")
    private String parentId;

    @Column(name = "CODE_NAME")
    private String codeName;

    @Transient
    private List<CmnCodeListPo> cmnCodeList = new LinkedList<>();

    public String getCodeId() {
        return this.codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCodeDescription() {
        return this.codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }

    public String getDelFlag() {
        return this.delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getDataLength() {
        return this.dataLength;
    }

    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCodeName() {
        return this.codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public List<CmnCodeListPo> getCmnCodeList() {
        return this.cmnCodeList;
    }

    public void setCmnCodeList(List<CmnCodeListPo> cmnCodeList) {
        this.cmnCodeList = cmnCodeList;
    }
}