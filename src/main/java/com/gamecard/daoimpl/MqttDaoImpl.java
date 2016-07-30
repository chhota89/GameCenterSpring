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

	public boolean isSubcribe(String topic) throws MqttPersistenceException,InterruptedException {

		System.out.println("topic url match:" + topic);
		String broker = "tcp://localhost:1883";
		String clientId = "mqttpublish";
		boolean check = false;
		// 0(only one time) 1(at least one time) 2(exactly one time)
		int qos = 2;

		MemoryPersistence peristance=new MemoryPersistence();
		try {
			MqttClient client = new MqttClient(broker, clientId,peristance);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			client.connect(connOpts);
			check=client.isConnected();
			MqttMessage message = new MqttMessage("GAME_CENTER".getBytes());
			message.setRetained(true);
			message.setQos(qos);
			client.publish(topic, message);
			if(check==true)
			{
				System.out.println(check);
				return check;
			}
			Thread.sleep(20000);
			client.disconnect();
			System.out.println("connection is Disconnected to broker");
			return check;
			
		} catch (MqttException me) {
			me.printStackTrace();
		}
		return check;

	}
	
	
	public boolean message(String topic,String json) throws MqttPersistenceException,InterruptedException {

		System.out.println("topic url match:" + topic);
		String broker = "tcp://localhost:1883";
		String clientId = "mqttpublish";
		boolean check = false;
		// 0(only one time) 1(at least one time) 2(exactly one time)
		int qos = 1;
		MemoryPersistence peristance=new MemoryPersistence();
		try {
			MqttClient client = new MqttClient(broker, clientId,peristance);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			client.connect(connOpts);
			check=client.isConnected();
			MqttMessage message = new MqttMessage(json.getBytes());
			message.setRetained(true);
			message.setQos(qos);
			client.publish(topic, message);
			if(check==true)
			{
				System.out.println(check);
				return check;
			}
			Thread.sleep(20000);
			client.disconnect();
			System.out.println("connection is Disconnected to broker");
			return check;
			
		} catch (MqttException me) {
			me.printStackTrace();
		}
		return check;

	}

}
