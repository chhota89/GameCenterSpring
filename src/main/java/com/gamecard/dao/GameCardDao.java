package com.gamecard.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.gamecard.dto.GameCardDto;
import com.gamecard.dto.PlaystoreDto;


public interface GameCardDao {
	/*public List<GameCardDto> createApplist(String packagename);*/
	public PlaystoreDto getPlayStoreData(String packagename);
	public PlaystoreDto insertnewpackage(PlaystoreDto list,String packagename);
	
}
