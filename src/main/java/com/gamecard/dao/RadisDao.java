package com.gamecard.dao;

import java.util.ArrayList;
import java.util.List;

import com.gamecard.dto.PlaystoreDto;

public interface RadisDao {
	public void isredis(String redistopic );
	public boolean redisPublisher(String redistopic, String list); 
	
}
