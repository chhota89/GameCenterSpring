package com.gamecard.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class UserRepository implements Repository<User> {

	@Autowired
	private RedisTemplate<String, User> redisTemplate;

	public RedisTemplate<String, User> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, User> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void put(User user) {
		redisTemplate.opsForHash().put(user.getObjectKey(), user.getKey(), user);
	}

	public void delete(User key) {
		redisTemplate.opsForHash().delete(key.getObjectKey(), key.getKey());
	}

	public User get(User key) {
		return (User) redisTemplate.opsForHash().get(key.getObjectKey(), key.getKey());
	}

	public List<User> getObjects() {
		List<User> users = new ArrayList<User>();
		for (Object user : redisTemplate.opsForHash().values(User.OBJECT_KEY)) {
			users.add((User) user);
		}
		return users;
	}
	
	public Map<Object,Object> findAll(){
		return this.redisTemplate.opsForHash().entries(User.OBJECT_KEY);
	}
}