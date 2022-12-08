package com.sf.tool.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.sf.tool.dao.CmnCodeListDefDao;
import com.sf.tool.entity.CmnCodeListDefPo;
import com.sf.tool.entity.CmnCodeListPo;
import com.sf.tool.excel.ExcelDataSource;
import com.sf.tool.excel.ExcelLineIterator;
import com.sf.tool.mapping.CodeListMapping;
import com.sf.tool.utils.SqlConverter;

@Component
@ConditionalOnProperty(name = { "run" }, havingValue = "codeList")
public class CodeListRunner implements CommandLineRunner {

    private static final String path = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\info\\20201225_codeList";
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CmnCodeListDefDao cmnCodeListDefDao;

    public void run(String... args) throws Exception {
//        Arrays.stream(new File(path).listFiles((f, name) -> (name.endsWith("xlsx") && !name.endsWith("_skip.xlsx"))))
//                .peek(f -> System.out.println(f.getAbsolutePath()))
//                .map(f -> new ExcelDataSource(f))
//                .forEach(f -> {
////                    ExcelLineIterator.of(f, CmnCodeListDefPo.class, 1).collect().stream()
////                        .map(SqlConverter::toSql)
////                        .forEach(System.out::println);
////
////                    ExcelLineIterator.of(f, CmnCodeListPo.class, 2).collect().stream()
////                        .map(SqlConverter::toSql)
////                        .forEach(System.out::println);
//
//                    try {
//                        f.close();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                });

        this.transactionTemplate.executeWithoutResult(s -> {
        	s.setRollbackOnly();
//            Arrays.stream(new File(path).listFiles((f, name) -> (name.endsWith("xlsx") && !name.endsWith("_skip.xlsx"))))
//                .forEach(f -> {
//                    updateExcelCodeId(f, defList);
//                });
            
        	List<CmnCodeListDefPo> defList = new LinkedList<>();
			Arrays.stream(new File(path).listFiles((f, name) -> (name.endsWith("xlsx") && !name.endsWith("_skip.xlsx"))))
//			        .map(f -> new ExcelDataSource(f))
			        .forEach(file -> {
			        	ExcelDataSource f = new ExcelDataSource(file);
			        	ExcelLineIterator.of(f, CodeListMapping.class, 0).collect().stream()
			        		.map(CodeListRunner::parseRow)
			        		.forEach(d -> {
			        			setCodeId(d);
			        			
			        			defList.add(d);
			        		});
			        	try {
							f.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
			        	
			        	updateExcelCodeId(file, defList);
			        });
            
        });
    }

    private void setCodeId(CmnCodeListDefPo def) {
        if (StringUtils.isEmpty(def.getCodeId())) {
            if (def.getCmnCodeList().isEmpty()) {
                this.cmnCodeListDefDao.getCodeIdByCodeDesc(def.getCodeDescription())
                        .ifPresent(codeId -> setCodeId(def, codeId));
            } else {
            	cmnCodeListDefDao.listAllMatchCodeId(def.getCmnCodeList().get(0).getCodeValue(), 
            			def.getCmnCodeList().get(0).getCodeDescription()).stream()
            	.peek(c -> System.out.println(c))
            	.filter(s -> cmnCodeListDefDao.isMatchAll(s, def.getCmnCodeList()))
    			.findFirst()
    			.ifPresent(c -> {
    				System.out.println(c + ":" + def.getCodeDescription());
    				setCodeId(def, c);
    			});
            	
            	if (def.getCodeId() == null) {
            		setCodeId(def, cmnCodeListDefDao.getNextCodeId());
            		try {
            		    
            		    System.out.println(SqlConverter.toDeleteDefSql(def));
            		    System.out.println(SqlConverter.toDeleteListSql(def));
            		    
            			String sql = SqlConverter.toSql(def);
            			
						cmnCodeListDefDao.save(sql);
						
						System.out.println(sql + ";");
	        			
	        			def.getCmnCodeList().stream()
	        				.map(r -> SqlConverter.toSql(r) + ";")
	        				.forEach(System.out::println);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
            	}
            }
        } else {
            try {
                System.out.println(SqlConverter.toDeleteDefSql(def));
                System.out.println(SqlConverter.toDeleteListSql(def));
                
                String sql = SqlConverter.toSql(def);
                
                System.out.println(sql + ";");
                
                def.getCmnCodeList().stream()
                    .map(r -> SqlConverter.toSql(r) + ";")
                    .forEach(System.out::println);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void setCodeId(CmnCodeListDefPo def, String codeId) {
        def.setCodeId(codeId);
        def.getCmnCodeList().stream()
            .forEach(d -> d.setCodeId(codeId));
    }

    private static void updateExcelCodeId(File file, List<CmnCodeListDefPo> defList) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
                FileOutputStream outFile = new FileOutputStream(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.rowIterator();

            AtomicInteger idx = new AtomicInteger(0);
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (idx.getAndIncrement() == 0) {
                    continue;
                }

                try {
                    if (row.getCell(0) == null || StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
                    	row.createCell(0).setCellValue(((CmnCodeListDefPo) defList.get(idx.get() - 2)).getCodeId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            workbook.write(outFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CmnCodeListDefPo parseRow(CodeListMapping row) {
        CmnCodeListDefPo def = new CmnCodeListDefPo();

        def.setCodeId(row.getCodeId());
        def.setCodeDescription(row.getCodeDescription());
        def.setCmnCodeList(parseCodeListValue(row.getCodeListValue()));
        def.setDataLength(Integer.valueOf(def.getCmnCodeList().stream()
                .mapToInt(v -> v.getCodeValue().length())
                .max()
                .orElse(2)));

        if (StringUtils.isNotEmpty(def.getCodeId())) {
            setCodeId(def, def.getCodeId());
        }

        return def;
    }

    private static List<CmnCodeListPo> parseCodeListValue(String cellValue) {
        if (StringUtils.isBlank(cellValue)) {
            return Collections.emptyList();
        }

        AtomicInteger idx = new AtomicInteger(1);
        return Arrays.stream(cellValue.trim().split("\n"))
                .filter(StringUtils::isNotBlank)
                .map(line -> {
                    final CmnCodeListPo code = new CmnCodeListPo();
                    
                    String splitor = line.indexOf(":") > 0 ? ":" : "：";
                    
                    final String[] split = line.trim().replace("", "").split(splitor);
                    code.setCodeValue(split[0].trim());
                    code.setCodeDescription(split[1].trim());
                    if (split.length > 2) {
                        code.setDelFlag(split[2].trim());
                    }
                    if (split.length > 3) {
                        code.setCategoryId(split[3].trim());
                    }
                    code.setOrderId(idx.getAndIncrement());
                    return code;
                }).collect(Collectors.toList());
    }
}