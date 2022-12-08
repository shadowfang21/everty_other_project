package com.sf.tool.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import groovy.text.GStringTemplateEngine;
import groovy.text.TemplateEngine;

@Configuration
public class TableGeneratorConfig {
    @Bean
    public GStringTemplateEngine markupTemplateEngine() {
        return new GStringTemplateEngine();
    }

    @Value("${pkg:}")
    private String pkg;

    @Bean
    String defaultOutputPath() {
        try {
            return getClass().getResource(".").getPath().replaceAll("classes.*", "") + "/export/eloan-parent/";
        } catch (Exception e) {
            return ".";
        }
    }

    @Bean
    public TableJavaGenerator entityGenerator() {
        TableJavaGenerator g = new TableJavaGenerator();
        g.setOutputPath(defaultOutputPath() + "eloan-dao/src/main/java/com/scsb/eloan/${pkg}/entity/");
        g.setTemplateEngine((TemplateEngine) markupTemplateEngine());
        g.setTemplatePath("templates/entity.groovy");
        g.setFileNameFun(t -> t.toClassName() + "Entity");
        g.setPkg(this.pkg);
        return g;
    }

    @Bean
    public TableJavaGenerator daoGenerator() {
        TableJavaGenerator g = new TableJavaGenerator();
        g.setOutputPath(defaultOutputPath() + "eloan-dao/src/main/java/com/scsb/eloan/${pkg}/dao/");
        g.setTemplateEngine((TemplateEngine) markupTemplateEngine());
        g.setTemplatePath("templates/dao.groovy");
        g.setFileNameFun(t -> t.toClassName() + "Dao");
        g.setPkg(this.pkg);
        return g;
    }

    @Bean
    public TableJavaGenerator dtoGenerator() {
        TableJavaGenerator g = new TableJavaGenerator();
        g.setOutputPath(defaultOutputPath() + "eloan-service/src/main/java/com/scsb/eloan/${pkg}/bean/");
        g.setTemplateEngine((TemplateEngine) markupTemplateEngine());
        g.setTemplatePath("templates/dto.groovy");
        g.setFileNameFun(t -> t.toClassName() + "DTO");
        g.setPkg(this.pkg);
        return g;
    }

    @Bean
    public TableJavaGenerator serviceGenerator() {
        TableJavaGenerator g = new TableJavaGenerator();
        g.setOutputPath(defaultOutputPath() + "eloan-service/src/main/java/com/scsb/eloan/${pkg}/service/");
        g.setTemplateEngine((TemplateEngine) markupTemplateEngine());
        g.setTemplatePath("templates/service.groovy");
        g.setFileNameFun(t -> t.toClassName() + "Service");
        g.setPkg(this.pkg);
        return g;
    }
    @Bean
    public TableJavaGenerator formServiceGenerator() {
        TableJavaGenerator g = new TableJavaGenerator();
        g.setOutputPath(defaultOutputPath() + "eloan-service/src/main/java/com/scsb/eloan/${pkg}/service/form/");
        g.setTemplateEngine((TemplateEngine) markupTemplateEngine());
        g.setTemplatePath("templates/formService.groovy");
        g.setFileNameFun(t -> t.toClassName() + "FormService");
        g.setPkg(this.pkg);
        return g;
    }
    @Bean
    public TableJavaGenerator controllerGenerator() {
        TableJavaGenerator g = new TableJavaGenerator();
        g.setOutputPath(defaultOutputPath() + "eloan-controller/src/main/java/com/scsb/eloan/${pkg}/controller/");
        g.setTemplateEngine((TemplateEngine) markupTemplateEngine());
        g.setTemplatePath("templates/controller.groovy");
        g.setFileNameFun(t -> t.toClassName() + "Controller");
        g.setPkg(this.pkg);
        return g;
    }
}