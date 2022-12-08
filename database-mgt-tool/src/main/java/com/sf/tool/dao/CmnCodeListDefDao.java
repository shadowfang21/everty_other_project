package com.sf.tool.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sf.tool.entity.CmnCodeListPo;

@Repository
public class CmnCodeListDefDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(String sql) {
        this.jdbcTemplate.execute(sql);
    }

    public Optional<String> getCodeIdByCodeDesc(String codeDesc) {
        return Optional.ofNullable(DataAccessUtils.singleResult(
                this.jdbcTemplate.queryForList("SELECT code_id FROM CMN_CODE_LIST_DEF WHERE CODE_DESCRIPTION = ?",
                        String.class, codeDesc )));
    }
    
    public List<String> listAllMatchCodeId(String value, String desc) {
    	return this.jdbcTemplate.queryForList("SELECT distinct code_id FROM CMN_CODE_LIST WHERE code_value = ? and CODE_DESCRIPTION = ?",
                String.class, value, desc);
    }
    
    public boolean isMatchAll(String codeId, List<CmnCodeListPo> codeList) {
    	List<CmnCodeListPo> targetList = (List<CmnCodeListPo>) this.jdbcTemplate.query("select * from cmn_code_list where code_id = ?", 
    			new BeanPropertyRowMapper(CmnCodeListPo.class), codeId);
    	
    	if (targetList.size() != codeList.size()) {
    		return false;
    	}
    	
    	return codeList.stream()
    		.allMatch(s -> targetList.stream()
    				.anyMatch(t -> t.getCodeValue().equals(s.getCodeValue()) 
    						&& t.getCodeDescription().equals(s.getCodeDescription())));
    }

    public String getNextCodeId() {
        return (String) DataAccessUtils.singleResult(this.jdbcTemplate.queryForList(
                "SELECT  lpad(min(code_id) + 1, 5,'0') AS next_code  FROM ( SELECT code_id, lead(code_id, 1, '0') OVER (ORDER BY code_id) AS next_code_id FROM CMN_CODE_LIST_DEF ) d WHERE lpad(code_id + 1, 5,'0') != next_code_id",
                String.class));
    }
}
