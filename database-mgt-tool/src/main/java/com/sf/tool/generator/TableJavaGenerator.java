package com.sf.tool.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.sf.tool.dto.Table;

import groovy.text.TemplateEngine;

public class TableJavaGenerator implements Consumer<Table> {
    private static final Logger logger = LoggerFactory.getLogger(com.sf.tool.generator.TableJavaGenerator.class);

    private String templatePath;

    private String outputPath;

    private TemplateEngine templateEngine;

    private String pkg;

    private Function<Table, String> fileNameFun;

    public void accept(Table t) {
        String pkg = StringUtils.isEmpty(this.pkg) ? t.getTableName().substring(0, 3).toLowerCase() : this.pkg;
        String path = this.outputPath.replace("${pkg}", pkg);

        String fileName = this.fileNameFun.apply(t);

        String fullPath = path + fileName + ".java";

        File outputDir = new File(path);
        outputDir.mkdirs();

        logger.info(fullPath);

        Map<String, Object> binding = new HashMap<>();
        binding.put("table", t);
        binding.put("pkg", pkg);
        binding.put("fileName", fileName);

        try (FileWriter fw = new FileWriter(new File(fullPath))) {

            File file = (new ClassPathResource(this.templatePath)).getFile();

            this.templateEngine.createTemplate(file).make(binding).writeTo(fw);

            fw.flush();
        } catch (IOException | org.codehaus.groovy.control.CompilationFailedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getTemplatePath() {
        return this.templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getOutputPath() {
        return this.outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public TemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public Function<Table, String> getFileNameFun() {
        return this.fileNameFun;
    }

    public void setFileNameFun(Function<Table, String> fileNameFun) {
        this.fileNameFun = fileNameFun;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }
}