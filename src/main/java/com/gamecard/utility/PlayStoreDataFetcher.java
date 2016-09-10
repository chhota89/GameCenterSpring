package com.gamecard.utility;

import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.gamecard.daoimpl.GameCardApkDaoImpl;
import com.gamecard.daoimpl.GameCardDaoImpl;
import com.gamecard.daoimpl.MqttDaoImpl;
import com.gamecard.dto.PlaystoreDto;
import com.google.gson.Gson;

public class PlayStoreDataFetcher implements Runnable{
	
	private String packageName;
	private GameCardDaoImpl cardDaoImpl;
	private MqttDaoImpl mqttDaoImpl;
	private Gson gson;
	private String topicName;
	
	
	public PlayStoreDataFetcher(String packageName,GameCardDaoImpl cardDaoImpl,Gson gson,MqttDaoImpl mqttDaoImpl,String topicName){
		this.packageName=packageName;
		this.cardDaoImpl=cardDaoImpl;
		this.mqttDaoImpl=mqttDaoImpl;
		this.gson=gson;
		this.topicName=topicName;
	}

	public void run() {
		// TODO Auto-generated method stub
		System.out.println("I am called for "+packageName);
		PlaystoreDto packagerespo = cardDaoImpl.getPlayStoreData(packageName);
		
		if (packagerespo != null) {
			GameCardApkDaoImpl apkDaoImpl = new GameCardApkDaoImpl();
			//PlaystoreDto found = apkDaoImpl.createApkSiteDetails(packagerespo, newPackage);
			packagerespo=cardDaoImpl.insertnewpackage(packagerespo, packageName);
			
			packagerespo.setSuggestion(false);
			String jsonArray = gson.toJson(packagerespo);				
			// calling the mqtt message() to publish to the subscriber
			
			System.out.println("New json is  "+jsonArray);
			try {
				mqttDaoImpl.message(topicName, jsonArray);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
		}
		
	}

}
