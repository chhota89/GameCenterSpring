package com.gamecard.dao;

import java.util.ArrayList;
import java.util.List;

import com.gamecard.dto.PlaystoreDto;

public interface RadisDao {
	public String isredis(String topic,ArrayList<PlaystoreDto> packagelist);
	
	
}
