package com.gamecard.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamecard.daoimpl.GameCardApkDaoImpl;
import com.gamecard.daoimpl.GameCardDaoImpl;

import com.gamecard.dto.PlaystoreDto;

@RestController("abc")
public class GameCardController {
	
	@Autowired
	protected GameCardDaoImpl cardDaoImpl;
	@Autowired
	protected GameCardApkDaoImpl apkDaoImpl;
	
	@RequestMapping(value="/gamecard",method=RequestMethod.GET,headers="Accept=application/json",params="packagename")
	public PlaystoreDto gameCardList(@RequestParam ("packagename")String packagename)
	{
		System.out.println("gamecardlist");
		
		ArrayList<PlaystoreDto> list =new ArrayList<PlaystoreDto>();
		list=cardDaoImpl.getPlayStoreData(packagename);
		
		System.out.println("gamecardlist list:"+list);
		String version=list.get(0).getVersion();
		System.out.println("hiiii"+version);
		System.out.println();
		
		boolean found=apkDaoImpl.createApkSiteDetails(list,packagename);
		
		if(found==true)
		{
			cardDaoImpl.insertnewpackage(list, packagename);
		}
		
		
		for(PlaystoreDto cardDto :list)
		{
			if(list.get(0).getPackagename().equals(packagename))
			{
				System.out.println("in if statment");
			}
			return cardDto;
		}
		
		return null;
	}
	
}
