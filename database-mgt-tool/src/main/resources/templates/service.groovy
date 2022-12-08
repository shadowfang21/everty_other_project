package com.scsb.eloan.${pkg}.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scsb.eloan.${pkg}.bean.${table.toClassName()}DTO;
import com.scsb.eloan.${pkg}.dao.${table.toClassName()}Dao;
import com.scsb.eloan.${pkg}.entity.${table.toClassName()}Entity;
import com.scsb.eloan.plm.service.${table.getServiceName()};


@Service
public class ${table.toClassName()}Service implements ${table.getServiceName()}<${table.toClassName()}DTO, ${table.toClassName()}Entity, ${table.toClassName()}Dao> {
    
    @Autowired
    private ${table.toClassName()}Dao ${table.toCamelCase()}Dao;
    
    @Override
    public Class<${table.toClassName()}DTO> getDtoClass() {
        return ${table.toClassName()}DTO.class;
    }

    @Override
    public Class<${table.toClassName()}Entity> getEntityClass() {
        return ${table.toClassName()}Entity.class;
    }

    @Override
    public ${table.toClassName()}Dao getDao() {
        return ${table.toCamelCase()}Dao;
    }
}
