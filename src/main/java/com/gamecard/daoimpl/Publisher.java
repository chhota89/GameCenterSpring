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
	// private final Jedis publisherJedis;
	private final String channel_name;
	String list;

	public Publisher(String list, String channel) {
		System.out.println("publish jedis:" + list + " " + "channel:" + channel);
		// this.publisherJedis = publisherJedis;
		this.list = list;
		this.channel_name = channel;
	}

	public void start() {
		System.out.println("Type your message....exit for terminate");
		try {
			ObjectMapper mapper = new ObjectMapper();
			GamePackageListReq reqlist;
			reqlist = mapper.readValue(list, GamePackageListReq.class);
			System.out.println("list value:" + list);

			Jedis publisherJedis = new Jedis();
			long a = publisherJedis.publish(channel_name, list);
			System.out.println("publish value:" + a);
			
		} catch (Exception e) {
			System.out.println("IO failure while reading input, e");
		}
	}

}
