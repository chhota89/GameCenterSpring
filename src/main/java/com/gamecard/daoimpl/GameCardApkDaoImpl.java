package com.gamecard.daoimpl;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.gamecard.dao.GameCardApkDao;
import com.gamecard.dto.PlaystoreDto;

public class GameCardApkDaoImpl implements GameCardApkDao {

	public ArrayList<String> createApkSiteDetails(PlaystoreDto playstoreDto,String packagename) {
		ArrayList<String> apkSiteDetails = new ArrayList<String>();

		// System.out.println(apkSite);

		try {
			String url = "https://apk-dl.com/"+packagename;
			// fetch the document over HTTP
			Document doc = Jsoup.connect(url).userAgent("Chrome/51.0.2704.106 ").timeout(10000).get();

			// getting info class
			Elements info = doc.getElementsByClass("info");

			String title = info.select("[itemprop=name]").attr("content"); // getting
																			// title
			String version = info.select("[itemprop=softwareVersion]").attr("content"); // getting
																						// version

			System.out.println("play  version" + playstoreDto.getVersion());
			if (version.equals(playstoreDto.getVersion())) {
				System.out.println("found true");
			} else
				System.out.println("found false");

			String downLink = doc.getElementsByClass("btn-md").select("[rel=nofollow]").attr("href");// getting
																										// download
																										// link
			downLink = ("https:").concat(downLink.trim());
			System.out.println("downLink" + downLink);
			System.out.println("----------Dl-apk site data--------------");

			// getting game info
			System.out.println("Title: " + title);
			System.out.println("Version: " + version);

			if (title.equals("") || version.equals("")) {
				System.out.println("All data is not fetched");
			} else {
				apkSiteDetails.add(title);
				apkSiteDetails.add(version);
				apkSiteDetails.add(downLink);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apkSiteDetails;
	}

}
