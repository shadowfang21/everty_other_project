package com.sf.tool.runner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.sf.tool.dto.Table;
import com.sf.tool.generator.TableFormGroupGenerator;
import com.sf.tool.generator.TableJavaGenerator;
import com.sf.tool.service.SchemaInfoService;

@Component
@ConditionalOnProperty(name = { "run" }, havingValue = "tableInfo")
public class GenerateTableInfoRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(com.sf.tool.runner.GenerateTableInfoRunner.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private String defaultOutputPath;

    @Autowired
    private SchemaInfoService schemaInfoService;

    @Value("${table-prefix}")
    private String tablePrefix;

    public void run(String... args) throws Exception {
        logger.info(this.defaultOutputPath);
        Optional.<File>of(new File(this.defaultOutputPath)).filter(File::exists).ifPresent(t -> {
            try {
                FileUtils.forceDelete(t);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        for (String prefix : this.tablePrefix.split(",")) {
            List<Table> tableList = this.schemaInfoService.getTableList(prefix);

            tableList.stream().forEach(t -> {
               this.applicationContext.getBeansOfType(TableJavaGenerator.class).values().stream()
                   .forEach(c -> c.accept(t));
               this.applicationContext.getBeansOfType(TableFormGroupGenerator.class).values().stream()
                   .forEach(c -> c.accept(t));
            });
        }
    }
}