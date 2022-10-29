package com.sf.redmine.batch.op;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

public class Before311RedmineApplication {
	
	private static final String folder_from ="C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_MTG_Loan\\info\\20220503_redmine_relocate\\batch_upload\\filelist\\03\\";
	private static final String folder_to ="C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_MTG_Loan\\info\\20220503_redmine_relocate\\batch_upload\\before311\\03\\";
	
	public static void main(String... args) {
		Arrays.stream(new File(folder_from).listFiles())
			.filter(f -> f.isFile())
			.filter(f -> {
				try {
					BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
					
					LocalDate cd = attr.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					
					return cd.compareTo(LocalDate.of(2022,3,11)) <= 0;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			})
			.forEach(f -> {
				try {
					FileUtils.moveFile(f, new File(folder_to + f.getName()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
	}
	
}
