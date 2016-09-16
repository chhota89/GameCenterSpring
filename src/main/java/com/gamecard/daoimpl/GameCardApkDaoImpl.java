package com.gamecard.daoimpl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
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

	public static final String APK_DL_URL = "https://apk-dl.com/";
	private static final Logger log = Logger.getLogger(GameCardApkDaoImpl.class);

	public PlaystoreDto createApkSiteDetails(PlaystoreDto dto, String packagename) {

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

			/* checking the title or version is blank */
			if (title.equals("") || version.equals("")) {
				System.out.println("All data is not fetched");
			} else {
				apkSiteDetails.add(title);
				apkSiteDetails.add(version);
				apkSiteDetails.add(downLink);
				System.out.println("title of apk :" + title);
				System.out.println("version of apk :" + version);
				System.out.println("downlink of apk :" + downLink);
			}

			/*------checking the Google playstore version and  apk.dl version-----*/
			if (!version.equals(dto.getVersion())) {
				System.out.println("ready to update with new version");
				dto.setVersion(version);
				System.out
						.println("update version :------>" + dto.getVersion() + "dto value is:----->" + dto.toString());
				return dto;
			} else {
				System.out.println("version is same no change required");
				return dto;
			}

			/*
			 * if (version.equals(dto.getVersion())) {
			 * System.out.println("found true"); found = true; return found; }
			 * 
			 * else { System.out.println("found false"); return found; }
			 */

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		System.out.println("null value is return");
		return null;
	}

	public static String getApkDownloadLink(String packageName) {
		String apkSite = APK_DL_URL.concat(packageName);
		String downUrl = null;
		try {
			// fetch the document over HTTP
			Document doc = Jsoup.connect(apkSite).userAgent("Chrome/47.0.2526.80").timeout(10000).get();

			// getting download link
			String downLink = doc.getElementsByClass("download-btn")
					.select("[class=mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect fixed-size mdl-button--primary]")
					.attr("href");

			// checking whether link contains "HTTP"
			if (downLink.contains("http") == false)
				downLink = ("http://apk-dl.com").concat(downLink.trim());

			// scraping downLink to get download link
			Document doc1 = Jsoup.connect(downLink).userAgent("Chrome/47.0.2526.80").timeout(10000).get();
			downUrl = doc1.getElementsByTag("p").select("a[href]").attr("href");

			if (downUrl != "") {
				// adding "HTTP" to link if absent
				if (downUrl.contains("http") == false) {
					downUrl = ("http:").concat(downUrl);
				}
			} else {
				// no download link present
				downUrl = null;
			}

			return downUrl;
		} catch (UnknownHostException u) {
			u.printStackTrace();
			log.error(u);

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

}
