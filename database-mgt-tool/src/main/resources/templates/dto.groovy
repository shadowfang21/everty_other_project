package com.scsb.eloan.${pkg}.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ${table.tableComments}
 * <p>@Author Spencer</p>
 * <p>@Date: ${new Date()}</p>
 */
@Data
@NoArgsConstructor
@ApiModel(description = "${table.tableComments}")
public class ${table.toClassName()}DTO implements Serializable {
    private static final long serialVersionUID = 1L;
<%
    table.columns.each{ col ->
        
        out << "\t/** ${col.comment} **/\n"
        out << "\t@JsonProperty(\"${col.javaColumnName}\")\n"
        out << "\t@ApiModelProperty(value = \"${col.comment}\", required = false)\n"
        out << "\tprivate ${col.javaDataType} ${col.javaColumnName};\n\n";
    }
%>

}