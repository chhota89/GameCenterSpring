package com.gamecard.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

import com.gamecard.dao.MqttDao;
import com.gamecard.dao.RadisDao;
import com.gamecard.daoimpl.GameCardApkDaoImpl;
import com.gamecard.daoimpl.GameCardDaoImpl;
import com.gamecard.daoimpl.MqttDaoImpl;
import com.gamecard.daoimpl.RadisDaoImpl;
import com.gamecard.dto.GamePackageListReq;
import com.gamecard.dto.MqttDto;
import com.gamecard.dto.PlaystoreDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RestController("abc")

public class GameCardController<E> {

	@Autowired
	protected GameCardDaoImpl cardDaoImpl;
	@Autowired
	protected GameCardApkDaoImpl apkDaoImpl;
	@Autowired
	protected MqttDaoImpl mqttDaoImpl;
	@Autowired
	protected RadisDaoImpl radisDaoImpl;

	PlaystoreDto dto = new PlaystoreDto();
	ArrayList<PlaystoreDto> list = new ArrayList<PlaystoreDto>();

	/*-------------Single Package Operation-------------*/
	@RequestMapping(value = "/gamecard", method = RequestMethod.GET, headers = "Accept=application/json", params = "packagename")
	public PlaystoreDto gameCardList(@RequestParam("packagename") String packagename) {

		System.out.println("packagename" + packagename);

		/*-----Calling the find package method to find the package name in the db----*/
		list = cardDaoImpl.findPackage(packagename);

		if (list != null && list.size() > 0) {
			setgetvalue(list);
			return dto;
		} else {
			list = cardDaoImpl.getPlayStoreData(packagename);

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
	public ArrayList<E> reqpost(@RequestBody String a, HttpServletRequest req) {

		ArrayList<PlaystoreDto> list = new ArrayList<PlaystoreDto>();
		ArrayList<PlaystoreDto> list1 = new ArrayList<PlaystoreDto>();
		ObjectMapper mapper = new ObjectMapper();

		try {
			GamePackageListReq reqlist = mapper.readValue(a, GamePackageListReq.class);
			// System.out.println(reqlist.getPackageList());

			System.out.println("topic is:" + reqlist.getTopic());
			/*boolean result = mqttDaoImpl.isSubcribe(reqlist.getTopic());
			if (result == true) {
				String msg = "topic is inserted";
				ArrayList<MqttDto> arraymqttDto = new ArrayList<MqttDto>();
				MqttDto mqttDto = new MqttDto();
				mqttDto.setStatus(result);
				mqttDto.setMsg(msg);
				System.out.println("msg of mqtt is:"+mqttDto.getMsg()+"status is:"+mqttDto.getStatus());
				arraymqttDto.add(mqttDto);
				------radis pub sub-----
				radisDaoImpl.isredis(reqlist.getTopic(),reqlist.getPackageList());
				return (ArrayList<E>) arraymqttDto;
			}*/

			
			for (String b : reqlist.getPackageList()) {//finding the package name into data base and return the value
				System.out.println(b);
				list = cardDaoImpl.findPackage(b);
				System.out.println("List generated:" + list);
				if (list != null && list.size() > 0) {
					PlaystoreDto dto = new PlaystoreDto();
					dto.setId(((PlaystoreDto) list.get(0)).getId());
					dto.setGametittle(((PlaystoreDto) list.get(0)).getGametittle());
					dto.setGamedate(((PlaystoreDto) list.get(0)).getGamedate());
					dto.setCategory(((PlaystoreDto) list.get(0)).getCategory());
					dto.setPackagename(((PlaystoreDto) list.get(0)).getPackagename());
					dto.setSize(((PlaystoreDto) list.get(0)).getSize());
					dto.setVersion(((PlaystoreDto) list.get(0)).getVersion());
					dto.setDescription(((PlaystoreDto) list.get(0)).getDescription());
					dto.setIsgame(((PlaystoreDto) list.get(0)).getIsgame());
					// setgetvalue( list);
					list1.add(dto);
					radisDaoImpl.isredis(reqlist.getTopic(),list1);
					//return (ArrayList<E>) list1;
				}
			}
			return (ArrayList<E>) list1;
		}/*

				else {
					System.out.println("else is call");
					list = cardDaoImpl.getPlayStoreData(b);

					setgetvalue(list);
					if (list.size() > 0) {
						boolean found = apkDaoImpl.createApkSiteDetails(list, b);

						if (found == true) {
							cardDaoImpl.insertnewpackage(list, b);
						}
						list1.add(dto);
					}
				}
			}
			return (ArrayList<E>) list1;

		}*/ catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("null return");

		return null;

	}

	public void setgetvalue(List list) {
		// PlaystoreDto dto = new PlaystoreDto();
		dto.setId(((PlaystoreDto) list.get(0)).getId());
		dto.setGametittle(((PlaystoreDto) list.get(0)).getGametittle());
		dto.setGamedate(((PlaystoreDto) list.get(0)).getGamedate());
		dto.setCategory(((PlaystoreDto) list.get(0)).getCategory());
		dto.setPackagename(((PlaystoreDto) list.get(0)).getPackagename());
		dto.setSize(((PlaystoreDto) list.get(0)).getSize());
		dto.setVersion(((PlaystoreDto) list.get(0)).getVersion());
		dto.setDescription(((PlaystoreDto) list.get(0)).getDescription());
		dto.setIsgame(((PlaystoreDto) list.get(0)).getIsgame());

	}
}
