package com.gamecard.daoimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
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
import com.gamecard.dto.UserInfo;
import com.gamecard.dto.VedioModel;
import com.gamecard.utility.StringUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.accessibility.internal.resources.accessibility;

@Repository
public class GameCardDaoImpl implements GameCardDao {
	Session session = new AnnotationConfiguration().configure("app.cfg.xml").buildSessionFactory().openSession();
	private static final Logger log = Logger.getLogger(GameCardDaoImpl.class);
	
	public void destructor(){
		if(session!=null)
			session.close();
		session=null;
	}

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
			if (list != null && list.size() > 0) {// checking if list is null
				System.out.println("returning the find package list:" + list);
				// session.close();
				return list.get(0);

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
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

			// -----vedio download link----
			String vedioLink = doc.getElementsByClass("play-action-container").attr("data-video-url");
			if (vedioLink.equals(""))
				vedioLink = null;
			link.setVedioLink(getYoutubeVideoId(vedioLink));
			// -------image download-------

			List<String> imageList = new ArrayList<String>();
			Elements elements = doc.getElementsByClass("full-screenshot");
			for (int i = 0; i < elements.size(); i++) {
				String imageLink = elements.get(i).attr("src");
				// Reduce size of the image
				imageLink = StringUtility.compressImageUrl(imageLink, "h400");
				if (imageLink.contains("http:")) {
					imageList.add(imageLink);
				} else {
					imageLink = ("http:").concat(imageLink);
					imageList.add(imageLink);
				}
			}
			link.setApkLink(GameCardApkDaoImpl.getApkDownloadLink(packagename));
			link.setImageList(imageList);

			/*-------creating the json for the link------*/
			/*--------------logo and is game jsoup---------*/

			// Image url
			String iconUrl = doc.getElementsByClass("cover-container").select("[itemprop=image]").attr("src");
			if (!iconUrl.contains("http"))
				iconUrl = ("http:").concat(iconUrl);
			iconUrl = StringUtility.compressImageUrl(iconUrl, "w100");
			System.out.println("image url " + iconUrl);

			Gson gson = new Gson();
			String jsonArray = gson.toJson(link, DownloadLinkDato.class);
			System.out.println("json of the array list" + jsonArray);

			Elements g = doc.getElementsByClass("document-subtitle");
			Elements info = doc.getElementsByClass("meta-info");
			Elements desc = doc.getElementsByClass("show-more-content");

			String cat = g.select("[class=document-subtitle category]").attr("href").toLowerCase();
			System.out.println("g:" + g);
			System.out.println("Category:" + cat);
			if (cat.contains("game"))
				result = true;
			System.out.println("cateogry link:" + cat + " ,found :" + result);

		
			if (result) {

				// result = true;
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
					log.error(e);
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
				dto.setIconLink(iconUrl);
				dto.setJsonImageVedioLink(jsonArray);

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
				dto.setIconLink(null);
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
			dto.setIconLink(null);
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

		//Session session1 = new AnnotationConfiguration().configure("app.cfg.xml").buildSessionFactory().openSession();

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
		dto.setIconLink(list1.getIconLink());
		dto.setJsonImageVedioLink(list1.getJsonImageVedioLink());

		Transaction trn = session.beginTransaction();
		session.save(dto);// Storing the value into the DB
		trn.commit();
		//session1.close();

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

	public List<PlaystoreDto> getPlayStoreDto(List<String> packageList) {
		// TODO Auto-generated method stub
		String queryString = "from PlaystoreDto where packagename IN (:packageList)";
		try {
			Query query = session.createQuery(queryString);
			query.setParameterList("packageList", packageList);
			return query.list();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			log.error(exception);
			return null;
		}
	}

	public boolean saveUserInfo(UserInfo userinfo, boolean update) {
		Transaction trn = session.beginTransaction();
		try {
			if (update)
				session.update(userinfo);
			else
				session.save(userinfo);
			return true;
		} catch (Exception exception) {
			log.error(exception);
			return false;
		} finally {
			trn.commit();
		}
	}

	// Generate vedio link for gameList
	public List<VedioModel> getVedioLink(List<String> gameList) {
		System.out.println("Get combination for game " + gameList);
		List<PlaystoreDto> playStoreList = getPlayStoreDto(gameList);
		List<VedioModel> vedioLinkList = new ArrayList<VedioModel>();
		if (playStoreList != null) {
			for (PlaystoreDto pDto : playStoreList) {
				DownloadLinkDato downloadLinkDato = new Gson().fromJson(pDto.getJsonImageVedioLink(),
						DownloadLinkDato.class);
				// System.out.println(pDto.getPackagename()+" --->"+vedioLink);
				if (downloadLinkDato.getVedioLink() != null && !downloadLinkDato.getVedioLink().equals("")) {
					VedioModel vedioModel = new VedioModel();
					vedioModel.setApkLink(downloadLinkDato.getApkLink());
					vedioModel.setVedioLink(downloadLinkDato.getVedioLink());
					vedioModel.setPackageName(pDto.getPackagename());
					vedioModel.setGameTitle(pDto.getGametittle());
					vedioModel.setIconLink(pDto.getIconLink());

					vedioLinkList.add(vedioModel);

				}
			}
		}
		return vedioLinkList;
	}

	public List<VedioModel> genrateVedioList(String packageName) {
		GameSuggestion gameSuggestion = new GameSuggestion();
		System.out.println(packageName);
		List<VedioModel> list=getVedioLink(gameSuggestion.getCombinationForGame(packageName));
		gameSuggestion.destructorGameSuggestion();
		return list;
	}

	public UserInfo checkUserInfo(String userId) {
		return (UserInfo) session.get(UserInfo.class, userId);
	}
	
	public  String getYoutubeVideoId(String youtubeUrl)
    {
        String video_id="";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
        {

            String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches())
            {
                String groupIndex1 = matcher.group(7);
                if(groupIndex1!=null && groupIndex1.length()==11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

}
