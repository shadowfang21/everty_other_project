package com.sf.redmine.batch.op.def;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class AttachmentDefFactory {
	
	private static final String folder ="C:\\Users\\JohnFang21\\Desktop\\IBM\\SCSB_MTG_Loan\\info\\20220503_redmine_relocate\\batch_upload";
	
	public static List<AttachmentDef> getDefList() {
		return Arrays.stream(new File(folder).listFiles())
			.filter(File::isFile)
			.flatMap(f -> {
				try {
					return FileUtils.readLines(f, StandardCharsets.UTF_8).stream()
							.filter(line -> line.startsWith("#"))
							.map(line -> {
								AttachmentDef def = AttachmentDef.builder()
										.ir(Integer.parseInt(line.split("_")[0].substring(1)))
										.fileMonth(f.getName())
										.fileName(line.replaceFirst("#\\d{4}_", ""))
										.build();
								
								def.setExpectedMoveTo(folder + "\\uploaded\\" + def.getFileMonth() + "\\");
								def.setFileLocation(folder + "\\filelist\\" + def.getFileMonth() + "\\");
								def.setIgnoreMoveTo(folder + "\\mtg_ignore\\" + def.getFileMonth() + "\\");
								def.setBefore311MoveTo(folder + "\\before311\\" + def.getFileMonth() + "\\");
								
								return def;
							});
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			})
			.sorted(Comparator.comparing(AttachmentDef::getIr))
			.collect(Collectors.toList());
	}
	
}
