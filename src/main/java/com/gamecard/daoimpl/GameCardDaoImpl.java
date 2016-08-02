package com.gamecard.daoimpl;

import java.util.ArrayList;
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
import com.gamecard.dto.GameCardDto;
import com.gamecard.dto.PlaystoreDto;

@Repository
public class GameCardDaoImpl implements GameCardDao {
	Session session = new AnnotationConfiguration().configure("app.cfg.xml").buildSessionFactory().openSession();

	/*------cheking package name into database-----*/
	public PlaystoreDto findPackage(String packagename) {
		System.out.println("packagename is:" + packagename);
		ArrayList<PlaystoreDto> list;
		try {

			System.out.println("session is :" + session);
			Query query = session.createQuery("from PlaystoreDto where packagename=?");
			query.setParameter(0, packagename);
			System.out.println("query is :" + query);
			list = (ArrayList<PlaystoreDto>) query.list();
			if (list != null && list.size() > 0) {
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

		try {
			// fetch the document over HTTP
			String url = "https://play.google.com/store/apps/details?id=" + packagename;

			Document doc = Jsoup.connect(url).userAgent("Chrome/51.0.2704.106 ").timeout(10000).get();

			// getting game class element
			String t = doc.getElementsByClass("document-title").text();
			System.out.println("title is ---->" + t);

			Elements g = doc.getElementsByClass("document-subtitle");
			Elements info = doc.getElementsByClass("meta-info");
			Elements desc = doc.getElementsByClass("show-more-content");

			String categoury = g.select("[itemprop=genre]").text();
			System.out.println("categoury is :" + categoury);

			if (categoury.contains("&") || categoury.contains(" ")) {
				String[] fcat = categoury.split("&");
				System.out.println("& operater  is there" + fcat[0]);

				String[] fcat1 = categoury.split(" ");
				System.out.println("space is there" + fcat1[0]);
				categoury = fcat1[0];
			}
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
				dto.setGametittle(t);
				dto.setCategory(g.select("[itemprop=genre]").text());
				dto.setVersion(version);//**NOTE: check the version
				dto.setSize(info.select("[itemprop=fileSize]").text());
				dto.setGamedate(info.select("[itemprop=datePublished]").text());
				dto.setDescription(desc.select("[itemprop=description]").text());
				dto.setPackagename(packagename);
				dto.setIsgame(result);

			}

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
				/*Transaction trn = session.beginTransaction();
				session.save(dto);
				trn.commit();
				System.out.println("non game data has been save");*/
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

		} catch (Exception e) {
			// System.out.println("HI U HAVE ENTER THE WORG URL.......");
			dto.setGametittle(null);
			dto.setCategory(null);
			dto.setDescription(null);
			dto.setGamedate(null);
			dto.setIsgame(result);
			dto.setPackagename(packagename);
			dto.setSize(null);
			dto.setVersion(null);
			/*Transaction trn = session.beginTransaction();
			session.save(dto);
			trn.commit();
			System.out.println("non package data has been save");*/
			return dto;

		}
		return dto;
	}

	/*--------checking into the database and storing if not present--------*/
	public PlaystoreDto insertnewpackage(PlaystoreDto list1, String packagename) {
		System.out.println("ready to check for data base");

		PlaystoreDto dto = new PlaystoreDto();

		dto.setId(list1.getId());
		dto.setGametittle(list1.getGametittle());
		dto.setGamedate(list1.getGamedate());
		dto.setCategory(list1.getCategory());
		dto.setPackagename(list1.getPackagename());
		dto.setSize(list1.getSize());
		dto.setVersion(list1.getVersion());
		dto.setDescription(list1.getDescription());
		dto.setIsgame(list1.getIsgame());

		Transaction trn = session.beginTransaction();
		session.save(dto);
		trn.commit();
		//session.close();
		return dto;

	}

}
