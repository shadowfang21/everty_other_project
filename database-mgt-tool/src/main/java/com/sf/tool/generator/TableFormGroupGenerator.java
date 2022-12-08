package com.sf.tool.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.sf.tool.dto.Column;
import com.sf.tool.dto.Table;

import groovy.text.TemplateEngine;

@Component
public class TableFormGroupGenerator implements Consumer<Table> {
    @Autowired
    private TemplateEngine templateEngine;
    @Resource
    private String defaultOutputPath;
    
    private static final String templatePath = "templates/formGroup.groovy";
    private String outputPath;

    @PostConstruct
    public void init() {
        this.outputPath = (new File(this.defaultOutputPath)).getParent();
    }

    public void accept(Table t) {
        String fullPath = this.outputPath + "/" + t.getTableName() + ".ts";

        Map<String, Object> binding = new HashMap<>();
        binding.put("table", t);
        binding.put("utils", this);

        try (FileWriter fw = new FileWriter(new File(fullPath))) {

            File file = (new ClassPathResource(templatePath)).getFile();

            this.templateEngine.createTemplate(file).make(binding).writeTo(fw);

            fw.flush();
        } catch (IOException | org.codehaus.groovy.control.CompilationFailedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean skipValidators(Column col) {
        return Stream.<String>of(new String[] { "LIST_ID", "CASE_SN", "CREATED_BY", "UPDATED_BY" })
                .anyMatch(s -> col.getColumnName().endsWith(s));
    }

    public static String getValidators(Column col) {
        if ("String".equals(col.getJavaDataType())) {
            if (col.getDataLength().intValue() >= 300) {
                return String.format("CustomValidator.maxLength(%s)", Integer.valueOf(col.getDataLength().intValue() / 3));
            }
            return String.format("CustomValidator.maxLength(%s)", col.getDataLength());
        }
        if ("Long".equals(col.getJavaDataType()) || "Integer".equals(col.getJavaDataType())
                || "java.math.BigDecimal".equals(col.getJavaDataType())) {
            return String.format("CustomValidator.number(%s,%s)", col.getDataPrecision(), col.getDataScale());
        }
        return null;
    }
}