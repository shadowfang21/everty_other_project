package cc.jren.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ReportXmlLoader {
    
    public static Map<String, Object> loadXmlString(File file) {
        
        Map<String, Object> map = new LinkedHashMap<>();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            
            NodeList  nodes = (NodeList) xpath.evaluate("/jasperReport/field", doc, XPathConstants.NODESET);
            
            for (int i = 0; i < nodes.getLength(); i++) {
                String fieldName = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
                String fieldType = nodes.item(i).getAttributes().getNamedItem("class").getNodeValue();
                
                Object value = "java.util.ArrayList".equals(fieldType) ? createMockDetail(fieldName, xpath, doc) : 
                    createMockData(fieldName, fieldType);
                
                map.put(fieldName, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return map;
    }
    
    public static void exportPutPattern(String path, String templateId, Map<String, Object> map) {
        try (PrintWriter bw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path + "/" + templateId + "_java.txt"))))) {
            map.keySet().stream()
                .map(s -> "map.put(\"" + s + "\", \"\");")
                .forEach(bw::println);
            
            map.entrySet().stream()
                .filter(s -> s.getValue() instanceof List)
                .filter(s -> ((List) s.getValue()).size() > 0)
                .map(s -> ((Map) ((List) s.getValue()).get(0)))
                .flatMap(s -> s.keySet().stream())
                .map(s -> "map.put(\"" + s + "\", \"\");")
                .forEach(bw::println);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static final ObjectWriter obj = new ObjectMapper().writerWithDefaultPrettyPrinter();
    
    public static void exportFakeJson(String path, String templateId, Map<String, Object> map) {
        try {
            obj.writeValue(new File(path + "/" + templateId + "_json.txt"), map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Object createMockData(String fieldName, String fieldType) {
        if ("java.lang.String".equals(fieldType)) {
            return fieldName + new Random().nextInt(3000);
        } else if ("java.lang.Boolean".equals(fieldType)) {
            return new Random().nextBoolean();
        } else if ("java.lang.Double".equals(fieldType)) {
            return new Random().nextDouble();
        } else if ("java.lang.Long".equals(fieldType)) {
            return new Random().nextLong();
        } else if ("java.lang.Float".equals(fieldType)) {
            return new Random().nextFloat();
        } else if ("java.lang.Integer".equals(fieldType)) {
            return new Random().nextInt(3000);
        } else if ("java.lang.Short".equals(fieldType)) {
            return new Random().nextDouble();
        } else if ("java.math.BigDecimal".equals(fieldType)) {
            return new Random().nextInt(3000);
        } else {
            return new Random().nextInt(3000);
        }
    }
    private static List<Map<String, Object>> createMockDetail(String fieldName, XPath xpath, Document doc) throws XPathExpressionException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        NodeList nodes = (NodeList) xpath.evaluate("/jasperReport/subDataset[@name=\"" + fieldName + "\"]", doc, XPathConstants.NODESET);
        
        if (nodes.getLength() > 0) {
            NodeList fileds = (NodeList) xpath.evaluate("./field", nodes.item(0), XPathConstants.NODESET);
            
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < fileds.getLength(); i++) {
                String name = fileds.item(i).getAttributes().getNamedItem("name").getNodeValue();
                String type = fileds.item(i).getAttributes().getNamedItem("class").getNodeValue();
                
                if (!"java.util.ArrayList".equals(type)) {
                    map.put(name, createMockData(name, type));
                }
            }
            
            result.add(map);
            result.add(map);
        } else {
            System.out.println(fieldName + " not found");
        }
        return result;
    }
    
}
