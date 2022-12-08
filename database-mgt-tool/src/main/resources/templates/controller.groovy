package com.scsb.eloan.${pkg}.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scsb.eloan.cmn.model.CmnEndorsementWrapperModel;
import com.scsb.eloan.cmn.service.CmnCodeListService;
import com.scsb.eloan.cmn.service.CmnEndorsementOperationService;
import com.scsb.eloan.cmn.service.CmnEndorsementOperationService.CmnEndorsementAction;
import com.scsb.eloan.cmn.service.CmnEndorsementOperationService.CmnEndorsementParam;
import com.scsb.eloan.cmn.service.CmnEndorsementOperationService.EndorsementOperation;
import com.scsb.eloan.com.bean.ComCollateralMasterDTO;
import com.scsb.eloan.core.bean.NgRequest;
import com.scsb.eloan.core.bean.NgResponse;
import com.scsb.eloan.core.bean.NgResponseBuilder;
import com.scsb.eloan.entity.enums.ListType;
import com.scsb.eloan.${pkg}.model.${table.toClassName()}Model;
import com.scsb.eloan.${pkg}.service.form.${table.toClassName()}FormService;

@RestController
@RequestMapping("/${pkg}/mcs")
public class ${table.toClassName()}Controller {
    
    @Autowired
    private ${table.toClassName()}FormService ${table.toCamelCase()}FormService;
    
    @Autowired
    private CmnEndorsementOperationService cmnEndorsementOperationService;
    
    @PostMapping("/qry001")
    public NgResponse<${table.toClassName()}Model> openByPlus(@RequestBody NgRequest<CmnEndorsementWrapperModel<Map<String, Object>>> ngRequest) {
        
        return NgResponseBuilder.build(${table.toCamelCase()}FormService.openByPlus());
    }
    
    @PostMapping("/qry002")
    public NgResponse<${table.toClassName()}Model> openByTypeNo(
            @RequestBody NgRequest<CmnEndorsementWrapperModel<Map<String, Object>>> ngRequest) {
        return NgResponseBuilder.build(${table.toCamelCase()}FormService.openByTypeNo(ngRequest.getContent().getLongOfTypeNo()));
    }

    @PostMapping("/upd001")
    public NgResponse<${table.toClassName()}Model> save(
            @RequestBody NgRequest<CmnEndorsementWrapperModel<${table.toClassName()}Model>> ngRequest) {
        
        ${table.toClassName()}Model result = cmnEndorsementOperationService.executeWithCallBack(
                CmnEndorsementAction.<${table.toClassName()}Model>builder()
                .action(() -> ${table.toCamelCase()}FormService.save(ngRequest.getContent().getModel()))
                .operation(EndorsementOperation.createOrUpdate(
                        ngRequest.getContent().getEndorsement(), 
                        (r) -> CmnEndorsementParam.builder()
                            .endorsement(ngRequest.getContent().getEndorsement())
                            .typeNo(r.getListId())
                            .deletable()
                            .buildToSup(), 
                            () -> ngRequest.getContent().getIsComplete()))
                .build());
        
        return NgResponseBuilder.of(result)
                .put("endorsement", ngRequest.getContent().getEndorsement())
                .build();
    }
    
    @PostMapping("/del001")
    public NgResponse<${table.toClassName()}Model> delete(
            @RequestBody NgRequest<CmnEndorsementWrapperModel<String>> ngRequest) {
        
        cmnEndorsementOperationService.delete(ngRequest.getContent().getListId().get(), () -> {
            ${table.toCamelCase()}FormService.delete(ngRequest.getContent().getLongOfTypeNo());
        });
        
        return NgResponseBuilder.empty();
    }
}
