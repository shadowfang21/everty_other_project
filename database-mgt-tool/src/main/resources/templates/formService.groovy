package com.scsb.eloan.${pkg}.service.form;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scsb.eloan.${pkg}.model.${table.toClassName()}Model;
import com.scsb.eloan.${pkg}.bean.${table.toClassName()}DTO;
import com.scsb.eloan.${pkg}.service.${table.toClassName()}Service;

@Service
public class ${table.toClassName()}FormService {
    
    @Autowired
    private ${table.toClassName()}Service ${table.toCamelCase()}Service;
    
    public ${table.toClassName()}Model openByPlus() {
        ${table.toClassName()}Model result = new ${table.toClassName()}Model();
        
        return result;
    }
    
    public ${table.toClassName()}Model openByTypeNo(long listId) {
        ${table.toClassName()}Model result = new ${table.toClassName()}Model();
        
        return result;
    }
    
    @Transactional
    public ${table.toClassName()}Model save(${table.toClassName()}Model model) {
        
        return model;
    }
    
    @Transactional
    public void delete(long listId) {
        ${table.toCamelCase()}Service.deleteByListId(listId);
    }
}
