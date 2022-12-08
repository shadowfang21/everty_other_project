package com.sf.tool.excel;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelDataSource implements Closeable {
    private XSSFWorkbook workbook;

    public ExcelDataSource(File file) {
        try {
            this.workbook = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ExcelDataSource(InputStream inputStream) {
        try {
            this.workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public XSSFSheet getSheet(int index) {
        return this.workbook.getSheetAt(index);
    }

    public void close() throws IOException {
        if (this.workbook != null) {
            this.workbook.close();
        }
    }

    public int getNumberOfSheets() {
        return this.workbook.getNumberOfSheets();
    }
}