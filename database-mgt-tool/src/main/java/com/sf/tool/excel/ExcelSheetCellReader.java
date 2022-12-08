package com.sf.tool.excel;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSheetCellReader<T> implements Closeable {
    
    private Class<T> clz;
    private XSSFWorkbook workbook;
    private Sheet sheet;
    
    private int xOffset = 0;
    private int yOffset = 0;
    
    public void setOffset(int x, int y) {
        this.xOffset = x;
        this.yOffset = y;
    }
    
    public static <T> ExcelSheetCellReader<T> of(ExcelDataSource ds, Class<T> clz, int sheetIdx) throws IOException {
        return new ExcelSheetCellReader<>(ds, clz, sheetIdx);
    }
    
//  public static <T> ExcelSheetCellReader<T> of(InputStream inputStream, Class<T> clz, int sheetIdx) throws IOException {
//        return new ExcelSheetCellReader<>(inputStream, clz, sheetIdx);
//    }
//
//  private ExcelSheetCellReader(InputStream inputStream, Class<T> clz, int sheetIdx) throws IOException {
//      this.clz = clz;
//      
//      workbook = new XSSFWorkbook(inputStream);
//        sheet = workbook.getSheetAt(sheetIdx);
//  }
    private ExcelSheetCellReader(ExcelDataSource ds, Class<T> clz, int sheetIdx) throws IOException {
        this.clz = clz;
        sheet = ds.getSheet(sheetIdx);
    }
    
    public T collect() {
        try {
            T obj = clz.newInstance();
            
            getCellReference().stream()
                .map(p -> {
                    
                    Function<String, String> converter = getConverter(p.getRight().converter());
                    String cellValue = getCellValue(this.getCell(p.getRight().value()));
                    
                    return Pair.of(p.getLeft(), converter != null ? converter.apply(cellValue) : cellValue);
                })
                .map(p -> {
                    String setter = "set" + StringUtils.capitalize(p.getLeft().getName());
                    return Pair.of(setter, p.getRight());
                })
                .forEach(p -> {
                    try {
//                      logger.debug("method name : {}, value :{}", p.getLeft(), p.getRight());
                        
                        Method method = clz.getMethod(p.getLeft(), String.class);
                        method.invoke(obj, p.getRight());
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            
            return obj;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Map<Class<?>, Function<String, String>> cacheMap = new HashMap<>();
    
    private Function<String, String> getConverter(Class<?> clz) {
        
        if (clz == null || "void".equals(clz.getName())) {
            return null;
        }
        
        if (cacheMap.containsKey(clz)) {
            return cacheMap.get(clz);
        } else {
            try {
                Function<String, String> func = (Function<String, String>) clz.newInstance();
                this.cacheMap.put(clz, func);
                return func;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private List<Pair<Field, Cell>> getCellReference() {
        Field[] fields = clz.getDeclaredFields();
        
        return Arrays.stream(fields)
            .filter(f -> f.getAnnotation(Cell.class) != null)
            .map(f -> Pair.of(f, f.getAnnotation(Cell.class)))
            .collect(Collectors.toList());
    }
    
    private org.apache.poi.ss.usermodel.Cell getCell(String reference) {
        CellAddress addr = new CellAddress(reference);
        Row row = sheet.getRow(addr.getRow() + yOffset);
        return row.getCell(addr.getColumn() + xOffset);
    }
    
    public static final String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {
        String str = null;
        if (CellType.NUMERIC.equals(cell.getCellType())) {
            str = NumberToTextConverter.toText(cell.getNumericCellValue());
        } else if (CellType.FORMULA.equals(cell.getCellType())) {
            if (CellType.NUMERIC.equals(cell.getCachedFormulaResultType())) {
                str = NumberToTextConverter.toText(cell.getNumericCellValue());
            }else {
                str = cell.getStringCellValue();
            }
            if (StringUtils.isNotBlank(str)) { //去除空字串
                str = str.trim();
            } else {
                str = null;
            }
        } else {
            str = cell.toString();
            if (StringUtils.isNotBlank(str)) { //去除空字串
                str = str.trim();
            } else {
                str = null;
            }
        }
        return str;
    }

    @Override
    public void close() throws IOException {
        if (workbook != null) {
            workbook.close();
        }
    }
}