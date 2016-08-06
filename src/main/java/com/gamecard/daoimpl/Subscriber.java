package com.gamecard.daoimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gamecard.dto.GamePackageListReq;
import com.gamecard.dto.PlaystoreDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {

	PlaystoreDto packagerespo = new PlaystoreDto();
	ArrayList<PlaystoreDto> list1 = new ArrayList<PlaystoreDto>();
	GamePackageListReq reqlist;
	ObjectMapper mapper = new ObjectMapper();
	/*------publish the redis topic and packagelist-----*/
	@Override
	public void onMessage(String channel, String packagenamelist) {
		System.out.println("Message received from channel:  ................. " + channel + " Msg: " + packagenamelist);
		GameCardDaoImpl cardDaoImpl = new GameCardDaoImpl();
		try {
			reqlist = mapper.readValue(packagenamelist, GamePackageListReq.class);

			List<String> packageList = reqlist.getPackageList();
			for (String b : packageList) { // -------iterating with
											// the package
				System.out.println("array position:---" + b);
				packagerespo = cardDaoImpl.findPackage(b);// ----searching the
															// package name into
															// DB
				System.out.println("List generated:" + packagerespo);

				if (packagerespo == null) {// find null if not found into DB
					packagerespo = cardDaoImpl.getPlayStoreData(b);// ------calling
																	// the
																	// playstore
																	// url and
																	// performing
																	// the jsoup
																	// and
																	// validation
																	// of game
					System.out.println("else list :" + packagerespo);
					if (packagerespo != null) {
						GameCardApkDaoImpl apkDaoImpl = new GameCardApkDaoImpl();
						PlaystoreDto found = apkDaoImpl.createApkSiteDetails(packagerespo, b);// calling
																								// the
																								// apk-dl
																								// to
																								// create
																								// the
																								// jsoup
																								// and
																								// check
																								// version
						/* if (found == true) { */
						packagerespo=cardDaoImpl.insertnewpackage(packagerespo, b);// -----inserting
																		// the
																		// new
																		// package
																		// into
																		// DB
						// }
					}

				}
				String mqttTopic = reqlist.getTopic();// getting the mqtt topic
				Gson gson = new GsonBuilder().serializeNulls().create();// allowing
																		// gson
																		// to
																		// take
																		// the
																		// null
																		// and
																		// not
																		// null
																		// value
				String jsonArray = gson.toJson(packagerespo);// ------converting
																// the json of
																// the package
																// data
				System.out.println("list of json is" + jsonArray);
				MqttDaoImpl mqttDaoImpl = new MqttDaoImpl();
				mqttDaoImpl.message(mqttTopic, jsonArray);// calling the mqtt
															// message() to
															// publish to the
															// subcriber
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*-------- Redis Subcribing the topic------*/
	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		System.out
				.println("channel name:" + channel + " " + "sub channel is:" + subscribedChannels + "****************");
	}

}
