 package com.sf.tool.utils;
 
 import com.sf.tool.entity.CmnCodeListDefPo;
import com.sf.tool.entity.CmnCodeListPo;
 
 public final class SqlConverter
 {
   private static String warpStr(String str) {
     return (str == null) ? "NULL" : ("'" + str + "'");
   }
   
   public static String toDeleteDefSql(CmnCodeListDefPo data) {
       return String.format("delete from cmn_code_list_def where code_id = %s;", data.getCodeId());
   }
   public static String toDeleteListSql(CmnCodeListDefPo data) {
       return String.format("delete from CMN_CODE_LIST where code_id = %s;", data.getCodeId());
   }
   
   public static String toSql(CmnCodeListDefPo data) {
     return String.format("INSERT INTO CMN_CODE_LIST_DEF (CODE_ID,CODE_DESCRIPTION,DEL_FLAG,DATA_LENGTH,PARENT_ID,CODE_NAME) VALUES (%s,%s,%s,%s,%s,%s)", new Object[] {
           
           warpStr(data.getCodeId()), 
           warpStr(data.getCodeDescription()), 
           warpStr(data.getDelFlag()), data
           .getDataLength(), 
           warpStr(data.getParentId()), 
           warpStr(data.getCodeName()) });
   }
   
   public static String toSql(CmnCodeListPo data) {
     return String.format("INSERT INTO CMN_CODE_LIST (CODE_ID,CODE_VALUE,ORDER_ID,CODE_DESCRIPTION,DEL_FLAG,CATEGORY_ID) VALUES (%s,%s,%s,%s,%s,%s)", new Object[] {
           
           warpStr(data.getCodeId()), 
           warpStr(data.getCodeValue()), data
           .getOrderId(), 
           warpStr(data.getCodeDescription()), 
           warpStr(data.getDelFlag()), 
           warpStr(data.getCategoryId())
         });
   }
 }


/* Location:              C:\Users\JohnFang21\Desktop\IBM\SCSB_Loan\project\eloan-scsb-generator-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\sf\too\\utils\SqlConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */