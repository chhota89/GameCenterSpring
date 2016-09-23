package com.gamecard.daoimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.hibernate.SessionFactory;
import org.hibernate.search.query.dsl.impl.ConnectedQueryContextBuilder.HSearchEntityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.gamecard.dto.GamePackageListReq;
import com.gamecard.dto.PlaystoreDto;
import com.gamecard.dto.UserInfo;
import com.gamecard.utility.PlayStoreDataFetcher;
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
	//ApplicationContext context ;

	/*------publish the redis topic and packagelist-----*/
	@Override
	public void onMessage(String channel, String packagenamelist) {
		System.out.println("Message received from channel:  ................. " + channel + " Msg: " + packagenamelist);
		GameCardDaoImpl cardDaoImpl;
		cardDaoImpl = new GameCardDaoImpl();
		
		/*context = new ClassPathXmlApplicationContext("ThreadPoolExecutor.xml");
		ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");*/

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
				pDto.setSuggestion(false);
				String jsonArray = gson.toJson(pDto);
				System.out.println("list of json is" + jsonArray);				
				// calling the mqtt message() to publish to the subscriber
				mqttDaoImpl.message(reqlist.getTopic(), jsonArray);
			}
			
			//New Package list.
			List<String> newPackageList= reqlist.getPackageList();
			newPackageList.removeAll(dbPackageList);
			
			for(String newPackage:newPackageList){
				
				//taskExecutor.execute(new PlayStoreDataFetcher(newPackage, cardDaoImpl,gson,mqttDaoImpl,reqlist.getTopic()));

				packagerespo = cardDaoImpl.getPlayStoreData(newPackage);
				
				if(packagerespo.getIsgame())
					userGames.add(packagerespo.getPackagename());
				
				if (packagerespo != null) {
					GameCardApkDaoImpl apkDaoImpl = new GameCardApkDaoImpl();
					//PlaystoreDto found = apkDaoImpl.createApkSiteDetails(packagerespo, newPackage);
					packagerespo=cardDaoImpl.insertnewpackage(packagerespo, newPackage);
				}
				packagerespo.setSuggestion(false);
				String jsonArray = gson.toJson(packagerespo);				
				// calling the mqtt message() to publish to the subscriber
				mqttDaoImpl.message(reqlist.getTopic(), jsonArray);
				
			}
			
			/*taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
			try {
			  taskExecutor.getThreadPoolExecutor().awaitTermination(60, TimeUnit.SECONDS);
			} catch (IllegalStateException e) {
			  e.printStackTrace();
			} catch (InterruptedException e) {
			  e.printStackTrace();
			}
			taskExecutor.shutdown();    

			for(PlaystoreDto pDto:cardDaoImpl.getPlayStoreDto(newPackageList)){
				if(pDto.getIsgame())
					userGames.add(pDto.getPackagename());
			}*/
			
			boolean update=false;
			List<String> userGamesClone=new ArrayList<String>();
			userGamesClone.addAll(userGames);
			
			//check for userInfo
			UserInfo userInfo=cardDaoImpl.checkUserInfo(reqlist.getTopic());
			if(userInfo==null){
				//user is new user
				userInfo=new UserInfo();
				
				//Save the userGames in redis
				saveUserListInRedis(null,userGames);
			}else{
				update=true;
				//Retrieve user previous game record
				List<String> previousGames=new ArrayList<String>();
				for(PlaystoreDto playstoreDto:userInfo.getPlaystoreDtos())
					previousGames.add(playstoreDto.getPackagename());
								
				userGames.removeAll(previousGames);
				
				//Check for any new game downloaded of not
				if(userGames.size()!=0)
					saveUserListInRedis(previousGames, userGames);
			}
			
			//set userInfo
			userInfo.setPlaystoreDtos(cardDaoImpl.getPlayStoreDto(userGamesClone));
			userInfo.setAndroidVersion(reqlist.getVersion());
			userInfo.setManufacturer(reqlist.getManufacturer());
			
			userInfo.setUserId(reqlist.getTopic());
			cardDaoImpl.saveUserInfo(userInfo,update);
			
			//Generate suggestion for the user
			genrateSuggestion(userGamesClone,cardDaoImpl);
			
			cardDaoImpl.destructor();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void saveUserListInRedis(List<String> previousGames,List<String> newGames){
		GameSuggestion gameSuggestion=new GameSuggestion();
		gameSuggestion.createCombinationWithOlderUesrGame(previousGames,newGames);
		gameSuggestion.destructorGameSuggestion();
		//gameSuggestion.createZSet(newGames);
	}
	
	public void genrateSuggestion(List<String> userGame,GameCardDaoImpl cardDaoImpl){
		GameSuggestion gameSuggestion=new GameSuggestion();
		List<String> suggestionGame=gameSuggestion.suggestionList(userGame);
		List<PlaystoreDto> playstoreDtoslist=cardDaoImpl.getPlayStoreDto(suggestionGame);
		if(playstoreDtoslist!=null)
			sendDataToMqtt(playstoreDtoslist);
		gameSuggestion.destructorGameSuggestion();
	}
	
	public void sendDataToMqtt(List<PlaystoreDto> playstoreDtoslist){
		for(PlaystoreDto pDto:playstoreDtoslist){
			pDto.setSuggestion(true);
			String jsonArray = gson.toJson(pDto);
			System.out.println("Suggestion for game is ------->"+jsonArray);
			try {
				mqttDaoImpl.message(reqlist.getTopic(), jsonArray);
			} catch (MqttPersistenceException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*-------- Redis Subscribing the topic------*/
	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		System.out
				.println("channel name:" + channel + " " + "sub channel is:" + subscribedChannels + "****************");
	}
	

}
