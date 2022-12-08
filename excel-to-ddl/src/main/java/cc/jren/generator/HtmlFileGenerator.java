package cc.jren.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import cc.jren.model.HtmlColumn;
import cc.jren.model.Table;

public class HtmlFileGenerator {

	
	private static Template template;
	
	public static void toFile(Table table, List<HtmlColumn> columns, String filePath) {

		VelocityContext context = new VelocityContext();
		context.put("tableName", table.getTableName());
		context.put("tableDesc", table.getTableDesc());
		context.put("columns", columns);
		
		Template template = template(); 
		File file = new File(filePath + table.getTableName() + ".html");
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
			template = velocityEngine.getTemplate("/template/html_column.vm");
		} 
		return template;
	}
}
