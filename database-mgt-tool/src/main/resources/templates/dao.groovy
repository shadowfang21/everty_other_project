package com.scsb.eloan.${pkg}.dao;

import org.springframework.stereotype.Repository;
import com.scsb.eloan.${pkg}.entity.${table.toClassName()}Entity;
import com.scsb.eloan.plm.dao.BaseDao;
import com.scsb.eloan.plm.dao.CaseMasterBaseDao;
<%
if (table.containAppealFlag()) {
    out << "import com.scsb.eloan.plm.dao.CaseAppealFlagBaseDao;\n";
}
if (table.isDetail()) {
    out << "import com.scsb.eloan.plm.dao.BaseDetailDao;\n";
}
%>

@Repository
public interface ${table.toClassName()}Dao extends BaseDao<${table.toClassName()}Entity, Long>, CaseMasterBaseDao<${table.toClassName()}Entity><%
if (table.containAppealFlag()) {
    out << ", CaseAppealFlagBaseDao<${table.toClassName()}Entity>";
}
if (table.isDetail()) {
    out << ", BaseDetailDao<${table.toClassName()}Entity>";
}
%>{

}
