package cc.jren;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

public class ComponentApplication {
    
    private static final String path = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\project\\trunk\\eloan-parent\\eloan-angular-webapp\\angular-project\\src\\app\\pages\\";
    private static final String base = "C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_Loan\\project\\trunk\\eloan-parent\\eloan-angular-webapp\\angular-project\\";
    private static final Pattern declare = Pattern.compile("declarations\\s*\\:\\s*\\[((.*?))\\]", Pattern.DOTALL);
    
    private static final String[] target = new String[] {"Aca001Component","Acct003Component","Acct004Component","Apf001Component","Apr001Component","Apr002Component","Apr003Component","Bdt001Component","Bdt002Component","Bse001Component","Bse002Component","Bse003Component","Bse004Component","Bse005Component","Cbi001Component","Cbi002Component","Cbi003Component","Cbi004Component","Cci001Component","Cdc001Component","Cdc002Component","Cdc003Component","Cdc004Component","Cds001Component","Cds002Component","Cds003Component","Cds004Component","Cds005Component","Cds006Component","Cdt001Component","Cdt002Component","Cdt003Component","Cdt004Component","Cdt005Component","Cdt006Component","Cdt007Component","Cdt008Component","Cdt008OpenReportComponent","Cdt009Component","Cdt010Component","Cdt011Component","Cdt012Component","Cdt013Component","Cdt014Component","Cdt015Component","Cdt016Component","Cfm001Component","Col001Component","Col002Component","Col003Component","Col004Component","Col005Component","Col006Component","Col007Component","Col008Component","Col009Component","Col010Component","Col011Component","Col012Component","Col015Component","Col017Component","Cre001Component","Cre002Component","Cre003Component","Crt001Component","Crt002Component","Crt003Component","Crt004Component","Crv001Component","Crv002Component","Crv003Component","Crv004Component","Crv005Component","Crv006Component","Csp001Component","Csp002Component","Csp003Component","Csp004Component","Csp005Component","Csp006Component","Csp007Component","Csp008Component","Csp009Component","Csp010Component","Csp011Component","CtrlViewComponent","Cun001Component","Cun002Component","Eld001Component","EndorsementReportComponent","Frt001Component","Frt002Component","Frt003Component","Frt011Component","Frt012Component","Frt013Component","Frt021Component","Frt022Component","Frt023Component","Frt031Component","Frt032Component","Ici001Component","Idc001Component","Iic001Component","Iic003Component","Iic005Component","Iic007Component","Iic009Component","Iic011Component","Imf001Component","Imf002Component","Imf003Component","Int001Component","Int002Component","Int003Component","Int004Component","Int005Component","Int006Component","Int007Component","Int010Component","Pcr001Component","Pcr002Component","Pcr003Component","Pdt001Component","Pdt002Component","Pdt003Component","Pdt004Component","Pdt005Component","Pdt006Component","Pdt007Component","Pdt008Component","Pdt009Component","Pdt010Component","Pdt011Component","Pdt012Component","Pdt013Component","Pdt014Component","Pdt015Component","Pdt016Component","Pdt017Component","Pdt018Component","Plc001Component","Plc002Component","Plc003Component","Ppc001Component","Ppc002Component","Ppc003Component","Pre001Component","Psh001Component","Psh002Component","Pun001Component","Pun001RemoveComponent","Pun002Component","Pun003Component","Pun004Component","Quo001Component","Quo002Component","Rev001Component","Rev002Component","Rsa001Component","Shr001Component","Shr002Component","Shr003Component","Smd001Component","Ssf001Component","Ssf002Component","Wid001Component"};
    
    private static final String format = "'%s': { loadComp : import(\"%s\").then(m => %s), 'width':";
    
    public static void main(String[] args) throws IOException {
        File resource = new File(path);
        
        Map<String, String> moduleConfig = new LinkedHashMap<>();
        
        FileUtils.listFiles(resource, new String[] {"ts"}, true).stream()
            .filter(f -> f.getName().endsWith("module.ts"))
            .filter(f -> !f.getName().contains("rout"))
//            .filter(f -> f.getName().contains("endorsement-report.module"))
//            .peek(f -> System.out.println(f.getName()))
            .forEach(f -> {
                try {
                    String content = FileUtils.readFileToString(f, Charset.forName("UTF-8"));
                    
                    Matcher matcher = declare.matcher(content);
                    
                    if (matcher.find()) {
                        String group = matcher.group(1).replace("\r\n", "\n");
                        
                        Arrays.stream(group.split("\n"))
                            .map(l -> RegExUtils.removeAll(l, "\\/\\/.*"))
                            .map(l -> l.replace(",", ""))
                            .map(l -> l.trim())
                            .filter(StringUtils::isNotBlank)
                            .forEach(c -> moduleConfig.put(c, f.getPath().replace(base, "").replace("\\", "/")));
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        
        Arrays.stream(target)
            .forEach(m -> {
               String c = moduleConfig.get(m);
               System.out.println(String.format(format, m, c, m)); 
            });
    }
}

    