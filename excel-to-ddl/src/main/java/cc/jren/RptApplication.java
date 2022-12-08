package cc.jren;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import cc.jren.generator.RptServiceFileGenerator;
import cc.jren.loader.ExcelLoader;

public class RptApplication {
	
	public static final String path = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\info\\20210128_report_def";
	
	public static void main(String[] args) throws IOException {
		File resource = new File(path);
		
		Arrays.stream(resource.listFiles((File dir, String name) -> name.endsWith("xlsx") && !name.endsWith("_done.xlsx") && !name.startsWith("~$")))
			.flatMap(f -> ExcelLoader.loadRptDef(f.getAbsolutePath()).stream())
			.forEach(def -> {
				RptServiceFileGenerator.toFile(def, RptApplication.path + "\\");
			});
		
		System.out.println("=====================================================");
	}
	
}
