package cc.jren;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import cc.jren.generator.FileGenerator;
import cc.jren.generator.TableSheetToTable;
import cc.jren.loader.ExcelLoader;

public class Application {

	public static final String path = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\info\\20201120_db_column";
	
	public static void main(String[] args) throws IOException {
		File resource = new File(path);
		
		Arrays.stream(resource.listFiles((File dir, String name) -> name.endsWith("xlsx") && !name.endsWith("_done.xlsx") && !name.startsWith("~$")))
			.flatMap(f -> ExcelLoader.load(f.getAbsolutePath()).stream())
			.peek(p -> {
				System.out.println("table: " + p.get(0).getTableName());
			})
			.forEach(list -> {
				FileGenerator.toFile(TableSheetToTable.toTable(list), Application.path + "\\ddl\\");
			});
		
		System.out.println("=====================================================");
		Arrays.stream(new File(Application.path + "\\ddl\\").listFiles())
			.forEach(f -> System.out.println("@" + f.getAbsolutePath() + ";"));
	}

}
