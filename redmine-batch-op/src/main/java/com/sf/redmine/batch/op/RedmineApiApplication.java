package com.sf.redmine.batch.op;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

import com.sf.redmine.batch.op.def.AttachmentDefFactory;
import com.sf.redmine.batch.op.redmine.RedmineUploader;
import com.taskadapter.redmineapi.RedmineException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RedmineApiApplication {

	public static void main(String[] args) {
		
		RedmineUploader uploader = RedmineUploader.getInstance();
		
		AtomicInteger count = new AtomicInteger(0);
		
		AttachmentDefFactory.getDefList().stream()
			.filter(def -> {
				File file = new File(def.getFileLocation() + def.getFileName());
				return file.exists() && file.isFile();
			})
			.forEach(def -> {
				File file = new File(def.getFileLocation() + def.getFileName());
//				
				log.info(file.getName() + " start upload");
//				
				boolean isUploadable = uploader.check(def.getIr(), def.getFileName());
				
				log.info(file.getName() + " " + def.getIr() + " " + isUploadable);
				
				if (isUploadable) {
					try {
						log.info(file.getName() + " " + def.getIr() + " start upload");
						uploader.uploadAttachment(def.getIr(), file);
						try {
							log.info(file.getName() + " upload done, move to backup folder");
							FileUtils.moveFile(file, new File(def.getExpectedMoveTo() + def.getFileName()));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						count.incrementAndGet();
					} catch (RedmineException | IOException e) {
						throw new RuntimeException(e);
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		
		log.info("total upload count : " + count.get());
	}
}
