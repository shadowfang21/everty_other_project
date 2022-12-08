package cc.jren.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;


@Data
public class HtmlColumn {
    
    private String columnName;
    
    private String dateType;
    
    private String comment;
    
    private String type;
    
    private String typeDesc;
    
    private String notNull;
    
    private Integer length;
    
    private Integer decimal;
    
    private String javaColumnName;
    
    private String htmlDataType;
    
    private static final Pattern p = Pattern.compile("^(.*)(\\(.*(\\d{5,5}).*)$");
    
    public String getNumberMax() {
        return StringUtils.rightPad("", length, '9');
    }
    public String toDecimalMax() {
        return StringUtils.rightPad("", length - decimal, '9') + "." + StringUtils.rightPad("", decimal, '9');
    }
    
    @lombok.ToString.Include
    public Integer getInputLength() {
        if (getTypeDesc().contains("字") && !getTypeDesc().contains("英")) {
            return length / 3;
        } else {
            return length;
        }
    }
    
    @lombok.ToString.Include
    public String getCodeListId() {
        Matcher matcher = p.matcher(comment);
        
        if (matcher.matches() && matcher.groupCount() == 3) {
            return matcher.group(3);
        }
        return StringUtils.EMPTY;
    }
    
    @lombok.ToString.Include
    public String getPureComment() {
        Matcher matcher = p.matcher(comment);
        
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return comment;
    }
    
    @lombok.ToString.Include
    public boolean isSkip() {
        return isAudit() || isPk();
    }
    
    private boolean isAudit() {
        return (isCreatedUser() || isUpdatedTime() || isUpdatedUser() || isCreateTime());
    }

//    private boolean isDelFlag() {
//        return this.columnName.equals("DEL_FLAG");
//    }

    private boolean isCreateTime() {
        return this.columnName.equals("INSERT_TIMESTAMP");
    }

    private boolean isUpdatedTime() {
        return this.columnName.equals("UPDATE_TIMESTAMP");
    }

    private boolean isUpdatedUser() {
        return this.columnName.startsWith("UPDATED_BY");
    }

    private boolean isCreatedUser() {
        return this.columnName.startsWith("CREATED_BY");
    }

    public boolean isPk() {
        return this.columnName.contains("LIST_ID");
    }
            
}
