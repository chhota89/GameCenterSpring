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

			// getting game package name
			String pack = url.substring(url.indexOf("id=") + 3);

			// getting game info

			PlaystoreDto dto = new PlaystoreDto();
			dto.setGametittle(t.select("[class=id-app-title]").text());
			dto.setGeneric(g.select("[itemprop=genre]").text());
			dto.setVersion(info.select("[itemprop=softwareVersion]").text());
			dto.setSize(info.select("[itemprop=fileSize]").text());
			dto.setGamedate(info.select("[itemprop=datePublished]").text());
			dto.setPackagename(pack);

			String title = dto.getGametittle();
			String generic = dto.getGeneric();
			String version = dto.getVersion();
			String size = dto.getSize();
			String date = dto.getGamedate();
			String packagegame = dto.getPackagename();

			/*-------inserting into the data base------*/

			/*Session session = sessionFactory.openSession();
			Query query = session.createQuery("from PlaystoreDto where packagename=? ");
			query.setParameter(0, dto.getPackagename());
			List list = query.list();
			if (list.size() == 0) {
				Transaction trn = session.beginTransaction();
				session.save(dto);
				trn.commit();
				session.close();
				playStoreDetails.add(dto);

			}
*/
//			else {
				int id = dto.getId();
				System.out.println("id no:" + id);
				// showing game name
				System.out.println("Title of Game: " + dto.getGametittle());
				// showing genre of game
				System.out.println("Genre:" + dto.getGeneric());
				// showing software version
				System.out.println("CVersion: " + dto.getVersion());
				// showing file size
				System.out.println("File Size: " + dto.getSize());
				// showing publish date
				System.out.println("Update date: " + dto.getGamedate());
				// showing package name
				System.out.println("Package Name:" + pack);

				if (dto.getGametittle().equals("") || dto.getGeneric().equals("") || dto.getVersion().equals("")
						|| dto.getSize().equals("") || dto.getGamedate().equals("") || pack.equals("")) {
					System.out.println("All data is not fetched");
				} else {
					playStoreDetails.add(dto);
				}
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playStoreDetails;
	}

}
