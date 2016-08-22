package com.gamecard.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.gamecard.redis.User;

public class RedisMap{
	
	public static final String DOWNLOAED_APP="DOWNLOAED_APP2";
	
	@Autowired
	private RedisTemplate<String, Integer> redisTemplate;

	public RedisTemplate<String, Integer> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Integer> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void put(String packageName,int value) {
		redisTemplate.opsForHash().put(DOWNLOAED_APP, packageName, value);
	}
	
	public void increment(String packageName){
		redisTemplate.opsForHash().increment(DOWNLOAED_APP, packageName, 1);
	}

	public void delete(String packageName) {
		redisTemplate.opsForHash().delete(DOWNLOAED_APP, packageName);
	}

	public Integer get(String packageName) {
		return (Integer) redisTemplate.opsForHash().get(DOWNLOAED_APP, packageName);
	}

	public List<Integer> getObjects() {
		List<Integer> users = new ArrayList<Integer>();
		for (Object user : redisTemplate.opsForHash().values(DOWNLOAED_APP)) {
			users.add((Integer) user);
		}
		return users;
	}
	
	public Map<Object,Object> findAll(){
		return this.redisTemplate.opsForHash().entries(DOWNLOAED_APP);
	}

}
