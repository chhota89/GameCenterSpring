package com.gamecard.daoimpl;

import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub{
	 @Override
	    public void onMessage(String channel, String message)
	    {
	            System.out.println("Message received from channel: "+channel+ " Msg: " + message);
	    }

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {

		System.out.println("channel name:"+channel+" "+"sub channel is:"+subscribedChannels);
		
	}

}
