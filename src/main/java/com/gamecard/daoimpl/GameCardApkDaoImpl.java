package com.gamecard.daoimpl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gamecard.dao.GameCardApkDao;
import com.gamecard.dto.PlaystoreDto;

@Repository
public class GameCardApkDaoImpl implements GameCardApkDao {

	@Resource(name = "sessionFactory")
	private SessionFactory sessionFactory;

	public boolean createApkSiteDetails(PlaystoreDto dto, String packagename) {
		
		boolean found = false;
		ArrayList<String> apkSiteDetails = new ArrayList<String>();

		try {
			String url = "https://apk-dl.com/" + packagename;
			// fetch the document over HTTP
			Document doc = Jsoup.connect(url).userAgent("Chrome/51.0.2704.106 ").timeout(10000).get();

			// getting info class
			Elements info = doc.getElementsByClass("info");

			String title = info.select("[itemprop=name]").attr("content"); // getting
																			// title

			String version = info.select("[itemprop=softwareVersion]").attr("content"); // getting
																						// version

			String downLink = doc.getElementsByClass("btn-md").select("[rel=nofollow]").attr("href");

			downLink = ("https:").concat(downLink.trim());
			System.out.println("downLink" + downLink);

			
			if (title.equals("") || version.equals("")) {
				System.out.println("All data is not fetched");
			} else {
				apkSiteDetails.add(title);
				apkSiteDetails.add(version);
				apkSiteDetails.add(downLink);
				System.out.println("title of apk :"+title);
				System.out.println("version of apk :"+version);
				System.out.println("downlink of apk :"+downLink);
			}
			
			
			/*------checking the Google playstore version and  apk.dl version-----*/
			if (version.equals(dto.getVersion())) {
				System.out.println("found true");
				found = true;
				return found;
			}

			else {
				System.out.println("found false");
				return found;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("null value is return");
		return false;
	}

}
