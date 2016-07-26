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
public class RadisDaoImpl  implements RadisDao   {

	public String isredis(final String topic,ArrayList<PlaystoreDto> list1) {
		
		@SuppressWarnings("resource")
		JedisPool jedispool = new JedisPool("localhost");
        final Jedis subscriberJedis = jedispool.getResource();
 
        final Subscriber subscriber = new Subscriber();
        new Thread(new Runnable(){
            public void run()
            {
                try
                {
                    System.out.println("Subscribing to " +topic);
                    subscriberJedis.subscribe(subscriber,topic);
                    System.out.println("Subscription ended.");
                }
                catch (Exception e)
                {
                    System.out.println("Subscribing failed."+e);
                }
            }
        }).start();
 
        Jedis publisherJedis = jedispool.getResource();
        new Publisher(list1,topic).start();
       // subscriber.unsubscribe();
        jedispool.returnResource(subscriberJedis);
        jedispool.returnResource(publisherJedis);
        return "success";
    }
		
		
}	
		
		
		
		
		
		
		
		
		/*RadisDaoImpl radisDaoImpl =new RadisDaoImpl();
		System.out.println("topic fromn mqtt:"+topic);
		System.out.println("package list from user:"+list);
		
		final String HOST = "127.0.0.1";
		Jedis jedis = new Jedis(HOST, 6379, 3000);
		jedis.set(topic,list.toString());
		jedis.subscribe(radisDaoImpl,topic);
		
		long a=jedis.publish(topic, "hello");
		System.out.println("publish value is: "+a);
		System.out.println("getting the jedis value"+jedis.get("test"));
		
	}

}
*/