package com.gamecard.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

	@RequestMapping(value = "/gamecard", method = RequestMethod.GET, headers = "Accept=application/json", params = "packagename")
	public PlaystoreDto gameCardList(@RequestParam("packagename") String packagename) {
		System.out.println("packagename" + packagename);
		PlaystoreDto dto = new PlaystoreDto();
		ArrayList<PlaystoreDto> list = new ArrayList<PlaystoreDto>();
		/*-----Calling the find package method to find the package name in the db----*/
		list = cardDaoImpl.findPackage(packagename);

		if (list != null && list.size() > 0) {
			dto.setId(list.get(0).getId());
			dto.setGametittle(list.get(0).getGametittle());
			dto.setGamedate(list.get(0).getGamedate());
			dto.setCategory(list.get(0).getCategory());
			dto.setPackagename(list.get(0).getPackagename());
			dto.setSize(list.get(0).getSize());
			dto.setVersion(list.get(0).getVersion());
			dto.setDescription(list.get(0).getDescription());
			dto.setIsgame(list.get(0).getIsgame());
			return dto;
		} else {
			list = cardDaoImpl.getPlayStoreData(packagename);

			dto.setId(list.get(0).getId());
			dto.setGametittle(list.get(0).getGametittle());
			dto.setGamedate(list.get(0).getGamedate());
			dto.setCategory(list.get(0).getCategory());
			dto.setPackagename(list.get(0).getPackagename());
			dto.setSize(list.get(0).getSize());
			dto.setVersion(list.get(0).getVersion());
			dto.setDescription(list.get(0).getDescription());
			dto.setIsgame(list.get(0).getIsgame());
			
			//System.out.println("gamecardlist list:" + list);
			
			if (list.size() > 0) {
				boolean found = apkDaoImpl.createApkSiteDetails(list, packagename);

				if (found == true) {
					cardDaoImpl.insertnewpackage(list, packagename);
				}
				return dto;
			} else
				return null;
		}
	}

}
