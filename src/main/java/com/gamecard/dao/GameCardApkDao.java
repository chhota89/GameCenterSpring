package com.gamecard.dao;

import java.util.ArrayList;

import com.gamecard.dto.PlaystoreDto;

public interface GameCardApkDao {
	public ArrayList<String> createApkSiteDetails(ArrayList<PlaystoreDto> dto,String packagename);

}
