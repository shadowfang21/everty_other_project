package com.sf.tool.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(CmnCodeListPo.class)
@Table(name = "CMN_CODE_LIST")
public class CmnCodeListPo implements Serializable {
    @Id
    @Column(name = "CODE_ID")
    private String codeId;
    @Id
    @Column(name = "CODE_VALUE")
    private String codeValue;
    @Column(name = "ORDER_ID")
    private Integer orderId;
    @Column(name = "CODE_DESCRIPTION", length = 200)
    private String codeDescription;
    @Column(name = "DEL_FLAG", length = 1)
    private String delFlag = "N";

    @Column(name = "CATEGORY_ID", length = 20)
    private String categoryId;

    public String getCodeId() {
        return this.codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCodeValue() {
        return this.codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    public Integer getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + ((this.codeId == null) ? 0 : this.codeId.hashCode());
        result = 31 * result + ((this.codeValue == null) ? 0 : this.codeValue.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CmnCodeListPo other = (CmnCodeListPo) obj;
        if (this.codeId == null) {
            if (other.codeId != null)
                return false;
        } else if (!this.codeId.equals(other.codeId)) {
            return false;
        }
        if (this.codeValue == null) {
            if (other.codeValue != null)
                return false;
        } else if (!this.codeValue.equals(other.codeValue)) {
            return false;
        }
        return true;
    }
}