package com.gamecard.daoimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.gamecard.dto.GamePackageListReq;
import com.gamecard.dto.PlaystoreDto;

import redis.clients.jedis.Jedis;

public class Publisher {
	
	private final String channel_name;
	String list;
	Jedis publisherJedis;
     /*-------constructor call with parameter json list and topic-------*/
	public Publisher(Jedis publisherJedis,String list, String channel) {
		System.out.println("publish jedis:" + list + " " + "channel:" + channel);
		this.list = list;
		this.channel_name = channel;
		this.publisherJedis=publisherJedis;
	}
	
	/*-------method call to redis publisher-------*/
	public void start() {
		System.out.println("in start()");
		try {
			ObjectMapper mapper = new ObjectMapper();
			GamePackageListReq reqlist;
			reqlist = mapper.readValue(list, GamePackageListReq.class);//reading the json and setting to dto class
			System.out.println("list value:" + list);

			long a = publisherJedis.publish(channel_name, list);//calling OnMessage() to publish message
			System.out.println("publish value:" + a);
			
		} catch (Exception e) {
			System.out.println("IO failure while reading input:"+e);
		}
	}

}
