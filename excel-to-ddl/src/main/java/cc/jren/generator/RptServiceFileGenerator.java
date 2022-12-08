package cc.jren.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import cc.jren.model.RptDef;

public class RptServiceFileGenerator {

	
	private static Template template;
	
	public static void toFile(RptDef def, String filePath) {

		VelocityContext context = new VelocityContext();
		context.put("rptId", def.getRptId());
		context.put("rptName", def.getRptName());

		Template template = template(); 
		File file = new File(filePath + def.getRptId() + "ReportService.java");
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
			template = velocityEngine.getTemplate("/template/rpt_service.vm");
		} 
		return template;
	}
	
	
}
