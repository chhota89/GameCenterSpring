package com.gamecard.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
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

	private static boolean subcribed = false;

	/*-------------Multiple Package Operation-------------*/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/package", headers = "Accept=application/json")
	@ResponseBody
	public E reqpost(@RequestBody String a) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			GamePackageListReq reqlist = mapper.readValue(a, GamePackageListReq.class);//reading the post url
			System.out.println(reqlist.getPackageList().toString());

			System.out.println("topic is:" + reqlist.getTopic());
			/*------radis pub sub-----*/
			boolean result = radisDaoImpl.redisPublisher("Play_Store", a);//calling the redispublisher(topic,request list) 
			String msg = "topic is inserted";
			ArrayList<MqttDto> arraymqttDto = new ArrayList<MqttDto>();// setting the result(true/false) and message and returning to client
			MqttDto mqttDto = new MqttDto();
			mqttDto.setStatus(result);
			mqttDto.setMsg(msg);
			System.out.println("msg of mqtt is:" + mqttDto.getMsg() + "status is:" + mqttDto.getStatus());
			arraymqttDto.add(mqttDto);

			return (E) arraymqttDto.get(0);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("null return");
		return null;
	}
	/*---------allowing the redis sub to start at the start of the tomcat server------- */
	@PostConstruct
	public void check() {
		System.out.println("Init method after properties are set : ");
		if (subcribed == false)//allowing the redis sub to start at one time
			{
			
			radisDaoImpl.isredis("Play_Store");//calling the redis sub
			subcribed = true;
		}
		System.out.println("on load if return call"+subcribed);
	}

}