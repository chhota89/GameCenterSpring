package com.gamecard.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamecard.dao.GameCardApkDao;
import com.gamecard.daoimpl.GameCardApkDaoImpl;
import com.gamecard.daoimpl.GameCardDaoImpl;
import com.gamecard.dto.GameCardDto;
import com.gamecard.dto.PlaystoreDto;

@RestController("abc")
public class GameCardController {
	
	@Autowired
	protected GameCardDaoImpl cardDaoImpl;
	
	@RequestMapping(value="/gamecard",method=RequestMethod.GET,headers="Accept=application/json",params="packagename")
	public PlaystoreDto gameCardList(@RequestParam ("packagename")String packagename)
	{
		System.out.println("gamecardlist");
		PlaystoreDto  dto=new PlaystoreDto();
		dto.setPackagename(packagename);

		ArrayList<PlaystoreDto> list =new ArrayList<PlaystoreDto>();
		list=cardDaoImpl.getPlayStoreData(packagename);
		System.out.println("gamecardlist list:"+list);
		System.out.println("gamecardlist list1:"+list.indexOf(dto.getVersion()));
		//GameCardApkDaoImpl impl=new GameCardApkDaoImpl();
		//impl.createApkSiteDetails(list.get(3),packagename);
		
		
		for(PlaystoreDto cardDto :list)
		{
			if(dto.getPackagename().equals(packagename))
			{
				System.out.println("in if statment");
			}
			return cardDto;
		}
		
		return null;
	}
	
}
