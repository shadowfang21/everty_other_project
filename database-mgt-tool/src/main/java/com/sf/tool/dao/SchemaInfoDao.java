package com.sf.tool.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sf.tool.dto.TableColumn;

@Repository
public class SchemaInfoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TableColumn> getTableInfo(String tableName) {
        StringBuffer sb = new StringBuffer("");
        sb.append(" select t.table_name, t.column_name, data_type, data_length, data_precision, data_scale, ");
        sb.append(" pk.constraint_name as pk_name, c.comments, tc.comments as table_comments ");
        sb.append(" from user_tab_cols t ");
        sb.append(" LEFT JOIN user_tab_comments tc ON tc.TABLE_NAME  = t.TABLE_NAME ");
        sb.append(" left join user_constraints p on p.table_name = t.table_name and p.constraint_type = 'P' ");
        sb.append(
                " left join user_cons_columns pk on pk.constraint_name = p.constraint_name and pk.column_name = t.column_name ");
        sb.append(" left join user_col_comments c on c.column_name = t.column_name and c.table_name = t.table_name ");
        sb.append(" where t.table_name like ? ");
        sb.append(" order by t.table_name, column_id ");

        return this.jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper(TableColumn.class), tableName);
    }
}
