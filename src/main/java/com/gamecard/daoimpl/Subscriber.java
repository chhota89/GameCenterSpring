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

import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {

	ArrayList<PlaystoreDto> list = new ArrayList<PlaystoreDto>();
	ArrayList<PlaystoreDto> list1 = new ArrayList<PlaystoreDto>();
	GamePackageListReq reqlist;
	ObjectMapper mapper = new ObjectMapper();

	@Override
	public void onMessage(String channel, String packagenamelist) {
		System.out.println("Message received from channel: " + channel + " Msg: " + packagenamelist);
		GameCardDaoImpl cardDaoImpl = new GameCardDaoImpl();
		try {
			reqlist = mapper.readValue(packagenamelist, GamePackageListReq.class);

			List<String> packageList = reqlist.getPackageList();
			for (String b : packageList) { // --------------------iterating with
											// the package
				System.out.println("array position:---" + b);
				list = cardDaoImpl.findPackage(b);
				System.out.println("List generated:" + list);

				if (list != null && list.size() > 0) {
					System.out.println("in if");
					// list1.add(dto);*/
					setgetvalue(list);
				} else {
					list = cardDaoImpl.getPlayStoreData(b);
					System.out.println("else list :" + list);
					setgetvalue(list);
					if (list != null && list.size() > 0) {
						GameCardApkDaoImpl apkDaoImpl = new GameCardApkDaoImpl();
						boolean found = apkDaoImpl.createApkSiteDetails(list, b);
						if (found == true) {
							cardDaoImpl.insertnewpackage(list, b);
						}
					}

				}
				Gson gson = new Gson();
				String jsonArray = gson.toJson(list);
				System.out.println("list of json is" + jsonArray);
				MqttDaoImpl  mqttDaoImpl=new MqttDaoImpl();
				mqttDaoImpl.message(channel, jsonArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		System.out.println("channel name:" + channel + " " + "sub channel is:" + subscribedChannels);
	}

	public void setgetvalue(List list) {
		PlaystoreDto dto = new PlaystoreDto();
		dto.setId(((PlaystoreDto) list.get(0)).getId());
		dto.setGametittle(((PlaystoreDto) list.get(0)).getGametittle());
		dto.setGamedate(((PlaystoreDto) list.get(0)).getGamedate());
		dto.setCategory(((PlaystoreDto) list.get(0)).getCategory());
		dto.setPackagename(((PlaystoreDto) list.get(0)).getPackagename());
		dto.setSize(((PlaystoreDto) list.get(0)).getSize());
		dto.setVersion(((PlaystoreDto) list.get(0)).getVersion());
		dto.setDescription(((PlaystoreDto) list.get(0)).getDescription());
		dto.setIsgame(((PlaystoreDto) list.get(0)).getIsgame());

	}

}
