package cc.jren;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.text.CaseUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cc.jren.enums.HtmlType;
import cc.jren.generator.HtmlFileGenerator;
import cc.jren.generator.TableSheetToTable;
import cc.jren.loader.ExcelLoader;
import cc.jren.model.Column;
import cc.jren.model.HtmlColumn;
import cc.jren.model.Table;

public class HtmlApplication {

	public static final String path = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\info\\20201120_db_column";
	
	public static void main(String[] args) throws IOException {
		File resource = new File(path);
		
		Arrays.stream(resource.listFiles((File dir, String name) -> name.endsWith("xlsx") && !name.endsWith("_done.xlsx") && !name.startsWith("~$")))
			.flatMap(f -> ExcelLoader.load(f.getAbsolutePath()).stream())
			.peek(p -> {
				System.out.println("table: " + p.get(0).getTableName());
			})
			.forEach(list -> {
			    
			    Table table = TableSheetToTable.toTable(list);
			    
			    HtmlFileGenerator.toFile(table,table.getColumns().stream()
			            .map(c -> {
			                HtmlColumn h = HtmlMapper.INSTANCE.toHtml(c);
			                h.setJavaColumnName(CaseUtils.toCamelCase(h.getColumnName(), false, '_'));
			                h.setHtmlDataType(HtmlType.find(h).name());
			                System.out.println(h);
			                return h;
			            })
			            .filter(s -> !s.isSkip())
			            .collect(Collectors.toList())
			            , Application.path + "\\html\\");
				
			});
		
		System.out.println("=====================================================");
	}
	
	@Mapper
	public interface HtmlMapper {
	    HtmlMapper INSTANCE = Mappers.getMapper(HtmlMapper.class);
	    
	    @Mapping(source = "logicNotNull", target = "notNull")
	    HtmlColumn toHtml(Column col);
	}
	
}
