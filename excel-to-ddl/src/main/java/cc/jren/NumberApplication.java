package cc.jren;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import cc.jren.loader.ExcelLoader;

public class NumberApplication {

	public static final String path = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\info\\20210320_parsingExcel";
	
	public static void main(String[] args) throws IOException {
		File resource = new File(path);
		
		Arrays.stream(resource.listFiles((File dir, String name) -> name.endsWith("xlsx") && !name.endsWith("_done.xlsx") && !name.startsWith("~$")))
			.flatMap(f -> ExcelLoader.loadNumber(f.getAbsolutePath()).stream())
			.forEach(list -> {
				System.out.println(list);
			});
		
//		System.out.println("=====================================================");
//		Arrays.stream(new File(NumberApplication.path + "\\ddl\\").listFiles())
//			.forEach(f -> System.out.println("@" + f.getAbsolutePath() + ";"));
	}

}
