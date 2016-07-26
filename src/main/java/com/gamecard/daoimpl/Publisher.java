package com.gamecard.daoimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.gamecard.dto.PlaystoreDto;

import redis.clients.jedis.Jedis;

public class Publisher {
	// private final Jedis publisherJedis;
	private final String channel_name;
	ArrayList<PlaystoreDto> list;

	public Publisher(ArrayList<PlaystoreDto> list, String channel) {
		System.out.println("publish jedis:" + list + " " + "channel:" + channel);
		// this.publisherJedis = publisherJedis;
		this.list = list;
		this.channel_name = channel;
	}

	public void start() {
		System.out.println("Type your message....exit for terminate");
		try {
			/*
			 * BufferedReader reader = new BufferedReader(new
			 * InputStreamReader(System.in));
			 */
			System.out.println("list value:" + list);
			/* while (true) { */
			/* String line = reader.readLine(); */
			Jedis publisherJedis = new Jedis();
			long a = publisherJedis.publish(channel_name, list.toString());
			System.out.println("publish value:" + a);
			if ("exit".equals(list)) {
				System.out.println("exit enter");
			}
			// }
		} catch (Exception e) {
			System.out.println("IO failure while reading input, e");
		}
	}

}
