package cc.jren.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import cc.jren.model.Table;

public class FileGenerator {

	
	private static Template template;
	
	public static void toFile(Table table, String filePath) {

		VelocityContext context = new VelocityContext();
		context.put("tableName", table.getTableName());
		context.put("tableDesc", table.getTableDesc());
		context.put("columns", table.getColumns());

		Template template = template(); 
		File file = new File(filePath + table.getTableName() + "_DDL.sql");
		file.getParentFile().mkdirs();

		try (Writer writer = new FileWriter(file)) {
			template.merge(context, writer);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		System.out.println("Output File: " + file.getPath());
	}

	private static Template template() {
		if (template == null) {
			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.setProperty("resource.loader", "class");
			velocityEngine.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			velocityEngine.init();
			template = velocityEngine.getTemplate("/template/oracle_table_ddl_template.vm");
		} 
		return template;
	}
}
