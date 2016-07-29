package com.gamecard.dao;

import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public interface MqttDao {
	
	public boolean isSubcribe(String topic)throws InterruptedException, MqttPersistenceException;
}
