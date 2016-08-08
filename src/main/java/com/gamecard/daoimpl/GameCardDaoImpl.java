package com.gamecard.daoimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.stereotype.Repository;

import com.gamecard.dao.GameCardDao;
import com.gamecard.dto.DownloadLinkDato;
import com.gamecard.dto.GameCardDto;
import com.gamecard.dto.PlaystoreDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.accessibility.internal.resources.accessibility;

@Repository
public class GameCardDaoImpl implements GameCardDao {
	Session session = new AnnotationConfiguration().configure("app.cfg.xml").buildSessionFactory().openSession();

	/*------cheking package name into database-----*/
	public PlaystoreDto findPackage(String packagename) {
		System.out.println("packagename is:" + packagename);
		ArrayList<PlaystoreDto> list;
		try {

			System.out.println("session is :" + session);
			Query query = session.createQuery("from PlaystoreDto where packagename=?");// Hibernte
																						// select
																						// query
																						// is
																						// fire
			query.setParameter(0, packagename);
			System.out.println("query is :" + query);
			list = (ArrayList<PlaystoreDto>) query.list();
			if (list != null && list.size() > 0) {// checking if list is null
				System.out.println("returning the find package list:" + list);
				// session.close();
				return list.get(0);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// session.close();
		return null;
	}

	/*------creating the jsoup file operation----*/
	public PlaystoreDto getPlayStoreData(String packagename) {
		boolean result = false;
		PlaystoreDto dto = new PlaystoreDto();
		DownloadLinkDato link = new DownloadLinkDato();

		try {
			// fetch the document over HTTP
			String url = "https://play.google.com/store/apps/details?id=" + packagename;

			Document doc = Jsoup.connect(url).userAgent("Chrome/51.0.2704.106 ").timeout(10000).get();

			// getting game class element
			String t = doc.getElementsByClass("document-title").text();
			System.out.println("title is ---->" + t);

			/*-----vedio download link----
			String downLink = doc.getElementsByClass("play-action-container").attr("data-video-url");
			System.out.println("playstore download link:-------->" + downLink);
			link.setVedioLink(downLink);
			-------image download-------
			
			List<String> imageList=new ArrayList<String>();
			Elements elements = doc.getElementsByClass("full-screenshot");
			for (int i = 0; i < elements.size(); i++) {
				System.out.println("imagelink count  :------> " + i);
				String imageLink = elements.get(i).attr("src");
				System.out.println("image link is :--------->" + imageLink);
				if (imageLink.contains("http:")) {
					imageList.add(imageLink);
				} else {
					imageLink = ("http:").concat(imageLink);
					imageList.add(imageLink);
				}
			}
			link.setImageList(imageList);
			*/
			/*-------creating the json for the link------*/
			Gson gson = new Gson();
			String jsonArray = gson.toJson(link,DownloadLinkDato.class);
			System.out.println("json of the array list" + jsonArray);

			Elements g = doc.getElementsByClass("document-subtitle");
			Elements info = doc.getElementsByClass("meta-info");
			Elements desc = doc.getElementsByClass("show-more-content");
			// System.out.println("describe"+desc);
			String categoury = g.select("[itemprop=genre]").text();
			System.out.println("categoury is :" + categoury);

			
			/*-----checking weather catefgoury contant & or some spacing----*/
			if (categoury.contains("&") || categoury.contains(" ")) {
				String[] fcat = categoury.split("&");
				System.out.println("& operater  is there" + fcat[0]);

				String[] fcat1 = categoury.split(" ");
				System.out.println("space is there" + fcat1[0]);
				categoury = fcat1[0];
			}
			/*----checking if categoury is game or not----*/
			if (categoury.equalsIgnoreCase("Action") || categoury.equalsIgnoreCase("Adventure")
					|| categoury.equalsIgnoreCase("Racing") || categoury.equalsIgnoreCase("Arcade")
					|| categoury.equalsIgnoreCase("Board") || categoury.equalsIgnoreCase("Card")
					|| categoury.equalsIgnoreCase("Casino") || categoury.equalsIgnoreCase("Casual")
					|| categoury.equalsIgnoreCase("Educational") || categoury.equalsIgnoreCase("Music")
					|| categoury.equalsIgnoreCase("Puzzle") || categoury.equalsIgnoreCase("Role Playing")
					|| categoury.equalsIgnoreCase("Simulation") || categoury.equalsIgnoreCase("Sports")
					|| categoury.equalsIgnoreCase("Strategy") || categoury.equalsIgnoreCase("Trivia")
					|| categoury.equalsIgnoreCase("Word")) {

				result = true;
				System.out.println(result);

				String version = info.select("[itemprop=softwareVersion]").text();
				/*------checking if version contant version no or not------*/
				try {
					if (version.equals("") || version.contains("Varies") == true) {
						String newVer = doc.getElementsByClass("recent-change").text();
						System.out.println("old new version:" + newVer);
						newVer = newVer.substring(newVer.indexOf(".") - 1, newVer.indexOf(".") + 5).trim();
						version = newVer.replaceAll("[^0-9.]", "");
						System.out.println("new version:" + version);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				/* setting all the jsoup value into the java dto */
				dto.setGametittle(t);
				dto.setCategory(g.select("[itemprop=genre]").text());
				dto.setVersion(version);// **NOTE: check the version
				dto.setSize(info.select("[itemprop=fileSize]").text());
				dto.setGamedate(info.select("[itemprop=datePublished]").text());
				dto.setDescription(desc.select("[itemprop=description]").text());
				dto.setPackagename(packagename);
				dto.setIsgame(result);
				dto.setJsonImageVedioLink(null);

			}
			/*-----this condistion come if it is not a game----*/
			else {
				System.out.println("it is not a game " + result);

				dto.setGametittle(t);
				dto.setCategory(null);
				dto.setDescription(null);
				dto.setGamedate(null);
				dto.setIsgame(result);
				dto.setPackagename(packagename);
				dto.setSize(null);
				dto.setVersion(null);
				dto.setJsonImageVedioLink(null);
				/*
				 * Transaction trn = session.beginTransaction();
				 * session.save(dto); trn.commit(); System.out.println(
				 * "non game data has been save");
				 */
				return dto;

			}

			/*
			 * if (dto.getGametittle().equals("") &&
			 * dto.getCategory().equals("") && dto.getVersion().equals("") &&
			 * dto.getSize().equals("") && dto.getGamedate().equals("") &&
			 * dto.getPackagename().equals("") &&
			 * dto.getDescription().equals("")) { System.out.println(
			 * "All data is not fetched"); } else { playStoreDetails.add(dto); }
			 */

			// showing game name
			System.out.println("Title of Game: " + dto.getGametittle());
			// showing genre of game
			System.out.println("Genre:" + dto.getCategory());
			// showing software version
			System.out.println("CVersion: " + dto.getVersion());
			// showing file size
			System.out.println("File Size: " + dto.getSize());
			// showing publish date
			System.out.println("Update date: " + dto.getGamedate());
			// showing package name
			System.out.println("Package Name:" + packagename);

		}
		/*-----handle the excption if it is not not a valide package----*/
		catch (Exception e) {
			// System.out.println("HI U HAVE ENTER THE WORG URL.......");
			dto.setGametittle(null);
			dto.setCategory(null);
			dto.setDescription(null);
			dto.setGamedate(null);
			dto.setIsgame(result);
			dto.setPackagename(packagename);
			dto.setSize(null);
			dto.setVersion(null);
			dto.setJsonImageVedioLink(null);
			/*
			 * Transaction trn = session.beginTransaction(); session.save(dto);
			 * trn.commit(); System.out.println("non package data has been save"
			 * );
			 */
			return dto;

		}
		return dto;
	}

	/*--------checking into the database and storing if not present--------*/
	public PlaystoreDto insertnewpackage(PlaystoreDto list1, String packagename) {
		System.out.println("ready to check for data base");

		PlaystoreDto dto = new PlaystoreDto();
		/* setting the list value into dto value and storing into DB */
		dto.setId(list1.getId());
		dto.setGametittle(list1.getGametittle());
		dto.setGamedate(list1.getGamedate());
		dto.setCategory(list1.getCategory());
		dto.setPackagename(list1.getPackagename());
		dto.setSize(list1.getSize());
		dto.setVersion(list1.getVersion());
		dto.setDescription(list1.getDescription());
		dto.setIsgame(list1.getIsgame());
		//dto.setJsonImageVedioLink(list1.getJsonImageVedioLink());

		Transaction trn = session.beginTransaction();
		session.save(dto);// Storing the value into the DB
		trn.commit();
		
		/* return your file from db after inserting into db */
		Query query = session.createQuery("from PlaystoreDto where packagename=?");// Hibernte
																					// select
																					// query
																					// is
																					// fire
		query.setParameter(0, packagename);
		System.out.println("query is :" + query);
		ArrayList<PlaystoreDto> list = (ArrayList<PlaystoreDto>) query.list();
		if (list != null && list.size() > 0) {// checking if list is null
			System.out.println("returning the find package list:" + list);
			return list.get(0);
		}
		// session.close();
		return dto;

	}

}
