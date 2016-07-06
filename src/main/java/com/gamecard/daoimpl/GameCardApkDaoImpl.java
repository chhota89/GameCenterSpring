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

	@Autowired
	SessionFactory factory;

	public ArrayList<String> createApkSiteDetails(ArrayList<PlaystoreDto> dto,String packagename) {
		ArrayList<String> apkSiteDetails = new ArrayList<String>();
		System.out.println("package name is"+packagename);

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

			System.out.println("play  version" + dto.get(0).getVersion());
			if (version.equals(dto.get(0).getVersion())) {
				System.out.println("found true");
				/*-------inserting into the data base------*/
				try{
			/*showing error*/	  Session session = factory.getCurrentSession(); 
				  System.out.println("session is stablish:"+session);
				  Query query =session.createQuery("from PlaystoreDto where packagename=? ");
				  System.out.println("query is fire:"+query);
				  query.setParameter(0, packagename); 
				  
				  List list =query.list(); 
				  System.out.println("list of file 2:"+list);
				  if (list.size() == 0) { 
					  System.out.println("in innerif");
					  Transaction trn =session.beginTransaction(); 
					  session.save(dto); 
					  trn.commit();
					  session.close(); 
					  //playStoreDetails.add(list2);
					  System.out.println("value submited in data");
				  }
				}
				catch(Exception e){
					e.printStackTrace();
					
				}
			
				
				
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
