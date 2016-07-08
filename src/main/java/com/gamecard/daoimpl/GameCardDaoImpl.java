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

	/*----taking the input from the url and perform check operation----*/
	public List<GameCardDto> createApplist(String packagename) {
		System.out.println("createApplist");
		System.out.println("packagename is" + packagename);

		Session session = sessionFactory.openSession();
		System.out.println("session is stablish" + session);

		Query query = session.createQuery("from GameCardDto where gamepackagename =?");
		System.out.println("query fire" + query);
		query.setParameter(0, packagename);

		List<GameCardDto> list = query.list();
		System.out.println("list to be return" + list);

		if (list != null && list.size() > 0) {
			System.out.println("in if");
			return list;
		} else {
			Transaction trn = session.beginTransaction();
			GameCardDto dto = new GameCardDto();
			dto.setGamepackagename(packagename);
			System.out.println("in else");
			session.save(dto);
			trn.commit();
			System.err.println("value is inserted");

			Query query2 = session.createQuery("from GameCardDto where gamepackagename =?");
			System.out.println("query fire" + query);
			query.setParameter(0, dto.getGamepackagename());

			List<GameCardDto> list2 = query.list();
			System.out.println("list to be return" + list);
			System.out.println("value retrive");
			return list2;

		}

	}

	/*------creating the jsoup file and performing the check operation----*/
	public ArrayList<PlaystoreDto> getPlayStoreData(String packagename) {

		ArrayList<PlaystoreDto> playStoreDetails = new ArrayList<PlaystoreDto>();
		try {
			// fetch the document over HTTP
			String url = "https://play.google.com/store/apps/details?id=" + packagename;
			Document doc = Jsoup.connect(url).userAgent("Chrome/51.0.2704.106 ").timeout(10000).get();

			// getting game title class
			Elements t = doc.getElementsByClass("document-title");

			// getting game info class
			Elements g = doc.getElementsByClass("document-subtitle");

			Elements info = doc.getElementsByClass("meta-info");
			Elements desc = doc.getElementsByClass("show-more-content");
			String se = desc.select("[itemprop=description]").text();
		System.out.println("disc");

			// getting game package name
			String pack = url.substring(url.indexOf("id=") + 3);

			PlaystoreDto dto = new PlaystoreDto();
			dto.setGametittle(t.select("[class=id-app-title]").text());
			dto.setCategory(g.select("[itemprop=genre]").text());
			dto.setVersion(info.select("[itemprop=softwareVersion]").text());
			dto.setSize(info.select("[itemprop=fileSize]").text());
			dto.setGamedate(info.select("[itemprop=datePublished]").text());
			dto.setPackagename(pack);
			dto.setDescription(desc.select("[itemprop=description]").text());
			

			String title = dto.getGametittle();
			String generic = dto.getCategory();
			String version = dto.getVersion();
			String size = dto.getSize();
			String date = dto.getGamedate();
			String packagegame = dto.getPackagename();
			String description=dto.getDescription();

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
			System.out.println("Package Name:" + pack);

			if (dto.getGametittle().equals("") || dto.getCategory().equals("") || dto.getVersion().equals("")
					|| dto.getSize().equals("") || dto.getGamedate().equals("") || pack.equals("")/*||dto.getDescription().equals("")*/) {
				System.out.println("All data is not fetched");
			} else {
				playStoreDetails.add(dto);
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playStoreDetails;
	}

	/*--------checking into the database and storing if not present--------*/
	public ArrayList<PlaystoreDto> insertnewpackage(ArrayList<PlaystoreDto> list1, String packagename) {
		System.out.println("ready to check for data base");

		PlaystoreDto dto = new PlaystoreDto();
		dto.setId(list1.get(0).getId());
		dto.setGametittle(list1.get(0).getGametittle());
		dto.setGamedate(list1.get(0).getGamedate());
		dto.setCategory(list1.get(0).getCategory());
		dto.setPackagename(list1.get(0).getPackagename());
		dto.setSize(list1.get(0).getSize());
		dto.setVersion(list1.get(0).getVersion());
		dto.setDescription(list1.get(0).getDescription());

		ArrayList<PlaystoreDto> play = new ArrayList<PlaystoreDto>();

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
