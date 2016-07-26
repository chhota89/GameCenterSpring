package com.gamecard.dao;

public interface MqttDao {
	
	public boolean isSubcribe(String topic)throws InterruptedException;
}
