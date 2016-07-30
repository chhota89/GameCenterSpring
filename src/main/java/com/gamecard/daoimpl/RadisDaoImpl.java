package com.gamecard.daoimpl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JEditorPane;

import org.springframework.stereotype.Repository;

import com.gamecard.dao.RadisDao;
import com.gamecard.dto.PlaystoreDto;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

@Repository
public class RadisDaoImpl implements RadisDao {

	public void isredis(final String redistopic) {

		@SuppressWarnings("resource")
		JedisPool jedispool = new JedisPool("localhost");
		final Jedis subscriberJedis = jedispool.getResource();

		final Subscriber subscriber = new Subscriber();
		new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("Subscribing to " + redistopic);
					subscriberJedis.subscribe(subscriber, redistopic);
					System.out.println("Subscription ended.");
				} catch (Exception e) {
					System.out.println("Subscribing failed." + e);
				}
			}
		}).start();

	}

	public String redisPublisher(String redistopic, String list) {
		JedisPool jedispool = new JedisPool("localhost");
		Jedis publisherJedis = jedispool.getResource();
		new Publisher(list, redistopic).start();
		//subscriber.unsubscribe();
		jedispool.returnResource(publisherJedis);
		return "success";

	}

}
