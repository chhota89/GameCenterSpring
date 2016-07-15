package com.gamecard.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.gamecard.daoimpl.GameCardApkDaoImpl;
import com.gamecard.daoimpl.GameCardDaoImpl;
import com.gamecard.dto.GamePackageListReq;
import com.gamecard.dto.PlaystoreDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RestController("abc")

public class GameCardController {

	@Autowired
	protected GameCardDaoImpl cardDaoImpl;
	@Autowired
	protected GameCardApkDaoImpl apkDaoImpl;
/*-------------Single Package Operation-------------*/
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

			// System.out.println("gamecardlist list:" + list);

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
	/*-------------Multiple Package Operation-------------*/
	@RequestMapping(value = "/package", headers = "Accept=application/json")
	@ResponseBody
	public ArrayList<PlaystoreDto> reqpost(@RequestBody String a, HttpServletRequest req) {

		ArrayList<PlaystoreDto> list = new ArrayList<PlaystoreDto>();
		ArrayList<PlaystoreDto> list1 = new ArrayList<PlaystoreDto>();
		ObjectMapper mapper = new ObjectMapper();

		try {
			GamePackageListReq reqlist = mapper.readValue(a, GamePackageListReq.class);
			// System.out.println(reqlist.getPackageList());
			for (String b : reqlist.getPackageList()) {
				System.out.println(b);
				list = cardDaoImpl.findPackage(b);
				System.out.println("List generated:" + list);
				if (list != null && list.size() > 0) {
					PlaystoreDto dto = new PlaystoreDto();
					dto.setId(list.get(0).getId());
					dto.setGametittle(list.get(0).getGametittle());
					dto.setGamedate(list.get(0).getGamedate());
					dto.setCategory(list.get(0).getCategory());
					dto.setPackagename(list.get(0).getPackagename());
					dto.setSize(list.get(0).getSize());
					dto.setVersion(list.get(0).getVersion());
					dto.setDescription(list.get(0).getDescription());
					dto.setIsgame(list.get(0).getIsgame());
					list1.add(dto);
				}

				else {
					System.out.println("else is call");
					list = cardDaoImpl.getPlayStoreData(b);
					PlaystoreDto dto = new PlaystoreDto();
					dto.setId(list.get(0).getId());
					dto.setGametittle(list.get(0).getGametittle());
					dto.setGamedate(list.get(0).getGamedate());
					dto.setCategory(list.get(0).getCategory());
					dto.setPackagename(list.get(0).getPackagename());
					dto.setSize(list.get(0).getSize());
					dto.setVersion(list.get(0).getVersion());
					dto.setDescription(list.get(0).getDescription());
					dto.setIsgame(list.get(0).getIsgame());
					//list1.add(dto);
					
					
					if (list.size() > 0) {
						boolean found = apkDaoImpl.createApkSiteDetails(list, b);

						if (found == true) {
							cardDaoImpl.insertnewpackage(list, b);
						}
						list1.add(dto);
					}
				}
			}
			return list1;

		} catch (JsonParseException e) {

			e.printStackTrace();
		} catch (JsonMappingException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		System.out.println("null return");

		return null;

	}

}
