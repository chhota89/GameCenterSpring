package com.gamecard.daoimpl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import com.gamecard.dao.GameCardDao;
import com.gamecard.dto.GameCardDto;
import com.gamecard.dto.PlaystoreDto;

@Repository
public class GameCardDaoImpl implements GameCardDao {
	@Resource(name = "sessionFactory")
	SessionFactory sessionFactory;

	/*------cheking package name into database-----*/
	public ArrayList<PlaystoreDto> findPackage(String packagename) {

		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from PlaystoreDto where packagename=?");
		query.setParameter(0, packagename);
		ArrayList<PlaystoreDto> list = (ArrayList<PlaystoreDto>) query.list();
		if (list != null && list.size() > 0) {
			return list;
		}

		return null;
	}

	/*------creating the jsoup file operation----*/
	public ArrayList<PlaystoreDto> getPlayStoreData(String packagename) {
		boolean result = false;
		ArrayList<PlaystoreDto> playStoreDetails = new ArrayList<PlaystoreDto>();
		try {

			// fetch the document over HTTP
			PlaystoreDto dto = new PlaystoreDto();
			String url = "https://play.google.com/store/apps/details?id=" + packagename;

			Document doc = Jsoup.connect(url).userAgent("Chrome/51.0.2704.106 ").timeout(10000).get();

			// getting game class element
			Elements t = doc.getElementsByClass("document-title");
			Elements g = doc.getElementsByClass("document-subtitle");
			Elements info = doc.getElementsByClass("meta-info");
			Elements desc = doc.getElementsByClass("show-more-content");

			String categoury = g.select("[itemprop=genre]").text();
			System.out.println("categoury is :" + categoury);

			if (categoury.contains("&")) {
				dto.setGametittle(t.select("[class=id-app-title]").text());
				dto.setCategory(null);
				dto.setDescription(null);
				dto.setGamedate(null);
				dto.setIsgame(result);
				dto.setPackagename(packagename);
				dto.setSize(null);
				dto.setVersion(null);
				sessionFactory.openSession().save(dto);
				System.out.println("non jgame with special char");
				playStoreDetails.add(dto);
				return playStoreDetails;
			} else if (categoury.contains(" ")) {
				String[] fcat = categoury.split(" ");

				System.out.println("space is there" + fcat[0]);
				categoury = fcat[0];
			}

			if (categoury.equalsIgnoreCase("Action") || categoury.equalsIgnoreCase("Adventure")
					|| categoury.equalsIgnoreCase("Racing") || categoury.equalsIgnoreCase("Arcade")
					|| categoury.equalsIgnoreCase("Board") || categoury.equalsIgnoreCase("Card")
					|| categoury.equalsIgnoreCase("Casino") || categoury.equalsIgnoreCase("Casual")
					|| categoury.equalsIgnoreCase("Educational") || categoury.equalsIgnoreCase("Music")
					|| categoury.equalsIgnoreCase("Puzzle") || categoury.equalsIgnoreCase("Role Playing")
					|| categoury.equalsIgnoreCase("Simulation") || categoury.equalsIgnoreCase("Sport")
					|| categoury.equalsIgnoreCase("Strategy")) {

				result = true;
				System.out.println(result);

				dto.setGametittle(t.select("[class=id-app-title]").text());
				dto.setCategory(g.select("[itemprop=genre]").text());
				dto.setVersion(info.select("[itemprop=softwareVersion]").text());
				dto.setSize(info.select("[itemprop=fileSize]").text());
				dto.setGamedate(info.select("[itemprop=datePublished]").text());
				dto.setDescription(desc.select("[itemprop=description]").text());
				dto.setPackagename(packagename);
				dto.setIsgame(result);

			}

			else {
				System.out.println("it is not a game " + result);

				dto.setGametittle(null);
				dto.setCategory(null);
				dto.setDescription(null);
				dto.setGamedate(null);
				dto.setIsgame(result);
				dto.setPackagename(packagename);
				dto.setSize(null);
				dto.setVersion(null);
				sessionFactory.openSession().save(dto);
				System.out.println("non game data has been save");
				playStoreDetails.add(dto);
				return playStoreDetails;

			}

			if (dto.getGametittle().equals("") && dto.getCategory().equals("") && dto.getVersion().equals("")
					&& dto.getSize().equals("") && dto.getGamedate().equals("") && dto.getPackagename().equals("")
					&& dto.getDescription().equals("")) {
				System.out.println("All data is not fetched");
			} else {
				playStoreDetails.add(dto);
			}

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
			ArrayList<PlaystoreDto> playStoreDetails1 = new ArrayList<PlaystoreDto>();
			PlaystoreDto dto = new PlaystoreDto();
			// System.out.println("HI U HAVE ENTER THE WORG URL.......");
			dto.setGametittle(null);
			dto.setCategory(null);
			dto.setDescription(null);
			dto.setGamedate(null);
			dto.setIsgame(result);
			dto.setPackagename(packagename);
			dto.setSize(null);
			dto.setVersion(null);
			playStoreDetails1.add(dto);
			return playStoreDetails1;

		}
		return playStoreDetails;
	}

	/*--------checking into the database and storing if not present--------*/
	public ArrayList<PlaystoreDto> insertnewpackage(ArrayList<PlaystoreDto> list1, String packagename) {
		System.out.println("ready to check for data base");

		ArrayList<PlaystoreDto> play = new ArrayList<PlaystoreDto>();

		PlaystoreDto dto = new PlaystoreDto();

		dto.setId(list1.get(0).getId());
		dto.setGametittle(list1.get(0).getGametittle());
		dto.setGamedate(list1.get(0).getGamedate());
		dto.setCategory(list1.get(0).getCategory());
		dto.setPackagename(list1.get(0).getPackagename());
		dto.setSize(list1.get(0).getSize());
		dto.setVersion(list1.get(0).getVersion());
		dto.setDescription(list1.get(0).getDescription());
		dto.setIsgame(list1.get(0).getIsgame());

		Session session = sessionFactory.openSession();
		System.out.println("session stablish " + session);
		Query query = session.createQuery("from PlaystoreDto where packagename=? ");
		query.setParameter(0, packagename);
		List list = query.list();

		if (list.size() == 0) {
			Transaction trn = session.beginTransaction();
			session.save(dto);
			trn.commit();
			session.close();
			play.add(dto);
			System.out.println("list add to play list:" + play);
			return play;
		} else
			return play;
	}

}
