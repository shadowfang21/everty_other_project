package com.scsb.eloan.${pkg}.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.scsb.eloan.entity.BaseEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ${table.tableComments}
 * <p>@Author Spencer</p>
 * <p>@Date: ${new Date()}</p>
 */
@Entity
@Table(name = "${table.tableName}")
@Data
@NoArgsConstructor
public class ${table.toClassName()}Entity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
<%
	table.columns.each{ col ->
		if (!col.isAudit()) {
    		out << "\t/** ${col.comment} **/\n"
    		if (col.isPk()) {
    			out << "\t@Id\n";
    			out << "\t@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = \"${table.toClassName()}SequenceGenerator\")\n"
    			out << "\t@SequenceGenerator(sequenceName = \"${col.comment}\", allocationSize = 1, name = \"${table.toClassName()}SequenceGenerator\")\n";
    		}
		    out << "\t@Column(name=\"${col.columnName}\"" + ((col.isCreateTime() || col.isCreatedUser()) ? ", updatable=false" : "") + ")\n"
            if (col.isDelFlag()) {
              out << "\tprivate ${col.javaDataType} ${col.javaColumnName} = DeleteFlag.N.getNumber();\n\n";
            } else {
              out << "\tprivate ${col.javaDataType} ${col.javaColumnName};\n\n";
            }
		}
	}
%>

}