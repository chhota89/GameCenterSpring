package com.gamecard.dao;

import java.util.ArrayList;

import com.gamecard.dto.PlaystoreDto;

public interface GameCardApkDao {
	public /*boolean */PlaystoreDto createApkSiteDetails(PlaystoreDto dto,String packagename);

}
