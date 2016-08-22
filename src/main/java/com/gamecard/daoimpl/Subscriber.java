package com.gamecard.daoimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.hibernate.SessionFactory;
import org.hibernate.search.query.dsl.impl.ConnectedQueryContextBuilder.HSearchEntityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gamecard.dto.GamePackageListReq;
import com.gamecard.dto.PlaystoreDto;
import com.gamecard.dto.UserInfo;
import com.gamecard.redis.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {

	PlaystoreDto packagerespo = new PlaystoreDto();
	ArrayList<PlaystoreDto> list1 = new ArrayList<PlaystoreDto>();
	GamePackageListReq reqlist;
	ObjectMapper mapper = new ObjectMapper();
	MqttDaoImpl mqttDaoImpl;
	Gson gson;
	Jedis jedis = new Jedis("localhost");
	GameCardDaoImpl cardDaoImpl;

	/*------publish the redis topic and packagelist-----*/
	@Override
	public void onMessage(String channel, String packagenamelist) {
		System.out.println("Message received from channel:  ................. " + channel + " Msg: " + packagenamelist);
		cardDaoImpl = new GameCardDaoImpl();
		List<String> userGames=new ArrayList<String>();
		gson = new GsonBuilder().serializeNulls().create();
		mqttDaoImpl = new MqttDaoImpl();
		try {
			reqlist = mapper.readValue(packagenamelist, GamePackageListReq.class);

			List<String> dbPackageList=new ArrayList<String>();
			//Check for package in database
			List<PlaystoreDto> playstoreDtoslist=cardDaoImpl.getPlayStoreDto(reqlist.getPackageList());
			for(PlaystoreDto pDto:playstoreDtoslist){
				dbPackageList.add(pDto.getPackagename());
				if(pDto.getIsgame())
					userGames.add(pDto.getPackagename());
				String jsonArray = gson.toJson(pDto);
				System.out.println("list of json is" + jsonArray);				
				// calling the mqtt message() to publish to thesubcriber
				mqttDaoImpl.message(reqlist.getTopic(), jsonArray);
			}
			
			//New Package list.
			List<String> newPackageList= reqlist.getPackageList();
			newPackageList.removeAll(dbPackageList);
			for(String newPackage:newPackageList){

				packagerespo = cardDaoImpl.getPlayStoreData(newPackage);
				
				if(packagerespo.getIsgame())
					userGames.add(packagerespo.getPackagename());
				
				if (packagerespo != null) {
					GameCardApkDaoImpl apkDaoImpl = new GameCardApkDaoImpl();
					PlaystoreDto found = apkDaoImpl.createApkSiteDetails(packagerespo, newPackage);
					packagerespo=cardDaoImpl.insertnewpackage(packagerespo, newPackage);
				}
			}
			
			//Save the userGames in redis
			saveUserListInRedis(userGames);
			
			
			//Genrate suggestion for the user
			genrateSuggestion(userGames);
			
			//set userInfo
			UserInfo userInfo=cardDaoImpl.checkUserInfo(reqlist.getTopic());
			boolean update=false;
			if(userInfo==null)
				userInfo=new UserInfo();
			else
				update=true;
			
			userInfo.setPlaystoreDtos(cardDaoImpl.getPlayStoreDto(reqlist.getPackageList()));
			userInfo.setAndroidVersion(reqlist.getVersion());
			userInfo.setManufacturer(reqlist.getManufacturer());
			
			userInfo.setUserId(reqlist.getTopic());
			cardDaoImpl.saveUserInfo(userInfo,update);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void saveUserListInRedis(List<String> userGame){
		new GameSuggestion().insertNewList(userGame);
	}
	
	public void genrateSuggestion(List<String> userGame){
		GameSuggestion gameSuggestion=new GameSuggestion();
		Map<String, Double> suggestion=gameSuggestion.suggestionList(userGame);
		List<String> suggestionGame=new ArrayList<String>(suggestion.keySet());
		List<PlaystoreDto> playstoreDtoslist=cardDaoImpl.getPlayStoreDto(suggestionGame);
		sendDataToMqtt(playstoreDtoslist);
	}
	
	public void sendDataToMqtt(List<PlaystoreDto> playstoreDtoslist){
		for(PlaystoreDto pDto:playstoreDtoslist){
			String jsonArray = gson.toJson(pDto);
			System.out.println("Suggestion for game is ------->"+jsonArray);
			try {
				mqttDaoImpl.message(reqlist.getTopic()+"Suggestion", jsonArray);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*-------- Redis Subcribing the topic------*/
	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		System.out
				.println("channel name:" + channel + " " + "sub channel is:" + subscribedChannels + "****************");
	}
	

}
