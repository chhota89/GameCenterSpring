package com.gamecard.daoimpl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JEditorPane;

import org.springframework.stereotype.Repository;

import com.gamecard.dao.RadisDao;
import com.gamecard.dto.PlaystoreDto;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;

@Repository
public class RadisDaoImpl implements RadisDao {
	
	public static final String REDIS_HOST="23.23.233.73";
	public static final int REDIS_PORT=15589;
	public static final String REDIS_PASSWORD="pc0924q4lm4fvabg06f638rivs9";
	
	/*---Subccribe the topic at time of page load----*/
	public void isredis(final String redistopic) {

		@SuppressWarnings("resource")
		//JedisPoolConfig config = new JedisPoolConfig();
		//JedisPool jedispool = new JedisPool(config,REDIS_HOST, REDIS_PORT, 5000, "pc0924q4lm4fvabg06f638rivs9");
		//JedisPool jedispool = new JedisPool("localhost");
		//-----connecting to jedis server
		//final Jedis subscriberJedis = jedispool.getResource();
		JedisShardInfo shardInfo = new JedisShardInfo(REDIS_HOST, REDIS_PORT);
		shardInfo.setPassword(REDIS_PASSWORD);
		final Jedis subscriberJedis = new Jedis(shardInfo);


		final Subscriber subscriber = new Subscriber();//-----object off subcriber
		new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("Subscribing to " + redistopic);
					subscriberJedis.subscribe(subscriber, redistopic);//-----call OnSubcribre message and sub topic
					System.out.println("Subscription ended.");
				} catch (Exception e) {
					System.out.println("Subscribing failed." + e);
				}
			}
		}).start();//------starting the thread

	}
	/*-------publish the topic and package list-----*/
	public boolean redisPublisher(String redistopic, String list) {
		System.out.println("redispublisher() call<--------------> ");
		//JedisPoolConfig config = new JedisPoolConfig();
		//JedisPool jedispool = new JedisPool(config,"23.23.233.73", 15589, 1000, "pc0924q4lm4fvabg06f638rivs9");//connecting to jedis
		//JedisPool jedispool = new JedisPool("localhost");
		JedisShardInfo shardInfo = new JedisShardInfo(REDIS_HOST, REDIS_PORT);
		shardInfo.setPassword(REDIS_PASSWORD);
		final Jedis publisherJedis = new Jedis(shardInfo);

		new Publisher(publisherJedis,list, redistopic).start();//------start method is call of Publisher class
		//subscriber.unsubscribe();
		//jedispool.returnResource(publisherJedis);
		return true;

	}

}
