package com.gamecard.daoimpl;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Repository;

import com.gamecard.dao.MqttDao;

@Repository
public class MqttDaoImpl implements MqttDao {
	
	private static final String BROKER="tcp://54.173.239.223:15691";
	private static final String USER_NAME="lxakiriz";
	private static final String PASSWORD="58IinVRYyVcJ";

	/*--------Method call during the mqqt sub-------*/
	public boolean isSubcribe(String topic) throws MqttPersistenceException,InterruptedException {

		System.out.println("topic url match:" + topic);
		//String broker = "tcp://52.66.116.176:1883";//MQTT Server 
		//String broker = "tcp://192.168.0.128:1883";//MQTT Server 
		String clientId = "mqttpublish";//assigning some client id
		boolean check = false;
		// 0(only one time) 1(at least one time) 2(exactly one time)
		int qos = 2;

		MemoryPersistence peristance=new MemoryPersistence();
		try {
			MqttClient client = new MqttClient(BROKER, clientId,peristance);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setUserName(USER_NAME);
			connOpts.setPassword(PASSWORD.toCharArray());
			client.connect(connOpts);//connecting with MQTT Server
			check=client.isConnected();
			MqttMessage message = new MqttMessage("GAME_CENTER".getBytes());
			message.setQos(qos);
			client.publish(topic, message);//publishing topic and client 
			if(check==true)
			{
				System.out.println(check);
				return check;
			}
			//Thread.sleep(20000);
			client.disconnect();
			System.out.println("connection is Disconnected to broker");
			return check;
			
		} catch (MqttException me) {
			me.printStackTrace();
		}
		return check;

	}
	
	/*-------method call to publish to the client------*/
	public boolean message(String topic,String json) throws MqttPersistenceException,InterruptedException {
		System.out.println("topic url match:" + topic);
		new MQTTthread(topic, json).run();
		return true;
	}
	
	
	
	
	class MQTTthread implements Runnable{
		String topic;
		String json;
		
		public MQTTthread(String topic,String json){
			this.topic=topic;
			this.json=json;
		}
		
		public void run() {
			//String broker = "tcp://52.66.116.176:1883";//MQTT Server
			String clientId = "mqttpublish";//redis Client ID
			boolean check = false;
			// 0(only one time) 1(at least one time) 2(exactly one time)
			int qos = 2;
			MemoryPersistence peristance=new MemoryPersistence();
			try {
				MqttClient client = new MqttClient(BROKER, clientId,peristance);
				MqttConnectOptions connOpts = new MqttConnectOptions();
				connOpts.setCleanSession(true);
				connOpts.setUserName(USER_NAME);
				connOpts.setPassword(PASSWORD.toCharArray());
				client.connect(connOpts);//connecting with MQTT 
				check=client.isConnected();
				MqttMessage message = new MqttMessage(json.getBytes());
				message.setQos(qos);
				client.publish(topic, message);//publishing topic and message
				//Thread.sleep(20000);
				client.disconnect();
				
			} catch (MqttException me) {
				me.printStackTrace();
			}
		}
	}

}
