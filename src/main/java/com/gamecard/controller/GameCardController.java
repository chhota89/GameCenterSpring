package com.gamecard.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gamecard.dao.MqttDao;
import com.gamecard.dao.RadisDao;
import com.gamecard.daoimpl.GameCardApkDaoImpl;
import com.gamecard.daoimpl.GameCardDaoImpl;
import com.gamecard.daoimpl.GameSuggestion;
import com.gamecard.daoimpl.MqttDaoImpl;
import com.gamecard.daoimpl.RadisDaoImpl;
import com.gamecard.daoimpl.Subscriber;
import com.gamecard.dto.GamePackageListReq;
import com.gamecard.dto.MqttDto;
import com.gamecard.dto.PlaystoreDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

@RestController("abc")
public class GameCardController<E> { 
	@Autowired
	protected GameCardDaoImpl cardDaoImpl;
	@Autowired
	protected GameCardApkDaoImpl apkDaoImpl;
	@Autowired
	protected MqttDaoImpl mqttDaoImpl;
	
	protected  static RadisDaoImpl radisDaoImpl=new RadisDaoImpl();
	
	private static final Logger log = Logger.getLogger(GameCardController.class);
	
 private static boolean subcribed = false;

	/*-------------Multiple Package Operation-------------*/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/package", headers = "Accept=application/json")
	@ResponseBody
	public E reqpost(@RequestBody String a) {
		ObjectMapper mapper = new ObjectMapper();
		log.info("/package is called --> "+a);
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
			log.error(e);
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
	
	//@RequestMapping(value = "/getVedioList" /*, params="packageName", method = RequestMethod.GET, produces = "application/json" */)
	
	@RequestMapping(value = "/getVedioList",params="packageName", method = RequestMethod.GET, produces = "application/json")
	public E getVedioLink(@RequestParam("packageName") String packageName){
		  //System.out.println("Vedio Link is "+cardDaoImpl.genrateVedioList("com.pinkpointer.math"));
		return (E) cardDaoImpl.genrateVedioList(packageName);
	}
	
	/*@RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("file") MultipartFile file){
        String name = "test11";
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = 
                        new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }*/
	
	@RequestMapping(value = "/redis")
	public void redisPractice(){
		
		  /*User user1 = new User("1", "user 1");
		  User user2 = new User("2","user 2");
		  userRepository.put(user1);
		  System.out.println(" Step 1 output : " + userRepository.get(user1));
		  userRepository.put(user2);
		  System.out.println(" Step 2 output : " + userRepository.getObjects());
		  
		  System.out.println("get user by id");
		  System.out.println(userRepository.get(user2));
		  
		  Map <Object,Object> personMatrixMap = userRepository.findAll();
		  System.out.println("Currently in the Redis Matrix");
		  System.out.println(personMatrixMap);
		  
		  userRepository.delete(user1);
		  System.out.println(" Step 3 output : " + userRepository.getObjects());
		  
		  personMatrixMap = userRepository.findAll();
		  System.out.println("Currently in the Redis Matrix");
		  System.out.println(personMatrixMap);*/
		
		/*System.out.println("List is ......");
		System.out.println(redisMap.findAll());*/
		
		Jedis jedis = new Jedis("localhost");
		//jedis.hincrBy("Download", "number", 1);
		//System.out.println("out put is+ "+jedis.hgetAll(Subscriber.PACKAGE_TABLE));
		
		GameSuggestion gameSuggestion=new GameSuggestion();
		String testArray[] = { "A B H G", "A E B C" , "A B C", "H G A", "A B C", "L M N"};
		for (int i = 0; i < testArray.length; i++) {
			String array[] = testArray[i].split(" ");
			Arrays.sort(array);
			gameSuggestion.createZSet(new ArrayList<String>(Arrays.asList(array)));
		}
		
		System.out.println("Combine game for A is "+gameSuggestion.getCombinationForGame("A"));
		
		/*jedis.zadd("mohmad44" ,1, "clash of clans");
		jedis.zadd("mohmad44" ,2, "clash of clans1");
		jedis.zadd("mohmad44" ,4, "clash of clans4");
		jedis.zadd("mohmad44" ,6, "clash of clans6");
		jedis.zadd("mohmad44" ,7, "clash of clans7");
		jedis.zadd("mohmad44" ,8, "clash of clans9");
		
		
		jedis.zincrby("mohmad44", 1, "clash of clans9");
		
		Set<String> elements = jedis.zrevrange("mohmad44", 0, -1);
		System.out.println(elements);*/
	}

}