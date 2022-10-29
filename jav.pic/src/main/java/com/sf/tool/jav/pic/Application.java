package com.sf.tool.jav.pic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@Log4j2
public class Application {
	
	private static final String url ="https://7mmtv.tv/zh/censored_avperformer/19616/RION/3.html";
	
	private static final String path = "C:\\Users\\JohnFang21\\Desktop\\web\\RION\\";
	
	public static void main(String[] args) throws IOException {
		
		OkHttpClient client = new OkHttpClient.Builder()
				.build();
		
		String response = client.newCall(new Request.Builder()
				.get()
				.url(url)
				.build())
			.execute().body().string();
		
		Document doc = Jsoup.parse(response);
//		
		Elements rows = doc.select(".latest-korean-box-row");
		
		rows.forEach(row -> {
			String poster = row.select(".latest-korean-box-img > a figure video").first().attr("poster").replace("/s/", "/b/");
			log.info("{}", poster);
			
			String title = row.select(".latest-korean-box-text h2").first().html().replace("<img src=\"/images/plyer_icon.jpg\">", "");
			
			log.info("{}", title);
			
			try (InputStream byteStream = client.newCall(new Request.Builder()
						.get()
						.url(poster)
						.build())
				.execute().body().byteStream()) {
				
				File target = new File(path + title + ".jpg");
				
				FileUtils.copyInputStreamToFile(byteStream, target);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		});
		
		
//		Elements imgBox = doc.select(".latest-korean-box-img > a");
//		
//		imgBox.stream()
//			.forEach(s -> {
//				String href = s.attr("href");
//				String poster = s.select("figure video").first().attr("poster");
//				
//				log.info("{}", href);
//				log.info("{}", poster);
//			});
	}

}
