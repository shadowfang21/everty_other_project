package cc.jren;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import cc.jren.loader.ReportXmlLoader;

public class FakeJsonApplication {
    
    private static final String path = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\project\\trunk\\eloan-parent\\eloan-controller\\src\\main\\resources\\ireport";
    
    public static final String export = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\info\\20210128_report_def\\export";
    
    public static void main(String[] args) throws IOException {
        File resource = new File(path);
        
        FileUtils.listFiles(resource, new String[]{"jrxml"}, true).stream()
            .filter(s -> s.getName().contains("crt001.jrxml"))
            .forEach(list -> {
                System.out.println(list);
                Map<String, Object> map = ReportXmlLoader.loadXmlString(list);
                ReportXmlLoader.exportFakeJson(export, list.getName(), map);
                ReportXmlLoader.exportPutPattern(export, list.getName(), map);
            });
        
        System.out.println("=====================================================");
    }
    
}
