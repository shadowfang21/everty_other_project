package com.sf.tool.excel;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelLineIterator<T> implements Iterator<T>, Closeable {
    
    private Class<T> clz;
    
    private Integer cellCount;
    private Iterator<Row> rowIterator;
    private XSSFWorkbook workbook;
    
    private Row nextRow = null;
    
    public static <T> ExcelLineIterator<T> of(ExcelDataSource ds, Class<T> clz, int sheetIdx) {
        return new ExcelLineIterator<>(ds, clz, sheetIdx);
    }
    
    public static <T> ExcelLineIterator<T> of(InputStream inputStream, Class<T> clz) {
        return new ExcelLineIterator<>(inputStream, clz);
    }
    
    private ExcelLineIterator(ExcelDataSource ds, Class<T> clz, int sheetIdx) {
        this.clz = clz;
        
        Sheet sheet = ds.getSheet(sheetIdx);
        rowIterator = sheet.rowIterator();
        if (rowIterator.hasNext()) {
            Row row = rowIterator.next(); //ignore header
            this.cellCount = row.getPhysicalNumberOfCells();
        }
        this.findNext();
    }
    
    private ExcelLineIterator(InputStream inputStream, Class<T> clz) {
        this.clz = clz;
        
        try {
            workbook = new XSSFWorkbook(inputStream);
                    
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            
            if (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                rowIterator = sheet.rowIterator();
                if (rowIterator.hasNext()) {
                    Row row = rowIterator.next(); //ignore header
                    this.cellCount = row.getPhysicalNumberOfCells();
                }
                this.findNext();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<T> collect() {
        List<T> result = new LinkedList<>();
        while (this.hasNext()) {
            result.add(this.next());
        }
        return result;
    }
    
    @Override
    public boolean hasNext() {
        return nextRow != null;
    }

    private void findNext() {
        while (rowIterator.hasNext()) {
            nextRow = rowIterator.next();
            if (!isBlankRow(nextRow)) {
                return;
            }
        }
//        try {
//            if (this.workbook != null) {
//                this.workbook.close();
//            }
//        } catch (IOException e) {
//            throw new RuntimeIOException(e);
//        }
        nextRow = null;
    }
    
    @Override
    public T next() {
        
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        Row row = nextRow;
        
        findNext();
        
        return this.convertToObject(row);
    }
    
    public static class IncludeNullCellIterator implements Iterator<Cell> {

        private Row row;
        private int cellCount;
        private int idx = 0;
        
        public IncludeNullCellIterator(Row row, int cellCount) {
            this.row = row;
            this.cellCount = cellCount;
        }

        @Override
        public boolean hasNext() {
            return idx < cellCount;
        }

        @Override
        public Cell next() {
            if (idx >= cellCount) {
                throw new NoSuchElementException();
            }
            return row.getCell(idx++);
        }

        public int getCellCount() {
            return cellCount;
        }
        
    }
    
    private T convertToObject(Row row) {
        
        try {
            T obj = clz.newInstance();
            
            Iterator<Cell> cellIterator = new IncludeNullCellIterator(row, cellCount);
            
            Arrays.stream(clz.getDeclaredFields())
                .map(f -> "set" + StringUtils.capitalize(f.getName()))
                .map(name -> {
                    try {
                        return clz.getDeclaredMethod(name, String.class);
                    } catch (NoSuchMethodException | SecurityException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(m -> cellIterator.hasNext())
                .forEach(m -> {
                    try {
                        final Cell cell = cellIterator.next();
                        
                        if (cell != null) {
                            m.invoke(obj, ExcelSheetCellReader.getCellValue(cell));
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private boolean isBlankRow(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            
            if (!CellType.BLANK.equals(cell.getCellType()) && 
                    cell.toString().trim().length() > 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void close() throws IOException {
        if (this.workbook != null) {
            workbook.close();
        }
    }

    public Integer getCellCount() {
        return cellCount;
    }
}