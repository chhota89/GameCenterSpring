package com.gamecard.daoimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gamecard.controller.GameCardController;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

public class GameSuggestion {

	JedisPoolConfig config;
	JedisPool jedispool;
	Jedis jedis;
	private static final double THRESHOLD = 0.0;
	private static final String DOWNLOAD_MAP = "DOWNLOAD_MAP24";
	private static final String GAME_SET = "GAME_SET24";
	private static final String COMBINE_MAP = "COMBINE_MAP24";

	private static final Logger log = Logger.getLogger(GameSuggestion.class);

	public GameSuggestion() {
		//config = new JedisPoolConfig();
		//jedispool = new JedisPool(config, "23.23.233.73", 15589, 5000, "pc0924q4lm4fvabg06f638rivs9");
		 //jedis = jedispool.getResource();
		//jedis = new Jedis("localhost");
		JedisShardInfo shardInfo = new JedisShardInfo(RadisDaoImpl.REDIS_HOST, RadisDaoImpl.REDIS_PORT);
		shardInfo.setPassword(RadisDaoImpl.REDIS_PASSWORD);
		jedis = new Jedis(shardInfo);
	}

	public void insertNewList(List<String> userGames) {
		Collections.sort(userGames);
		jedis.sadd(GAME_SET, userGames.toArray(new String[userGames.size()]));

		for (String packageName : userGames)
			increaceDownloadCount(packageName);

		/*
		 * genrateCombinationForList(userGames, remaningGame);
		 * userGames.removeAll(remaningGame);
		 */

		if (userGames.size() != 0) {
			// System.out.println("Game from user "+userGames);
			for (int i = 0; i < userGames.size() - 1; i++) {
				for (int j = i + 1; j < userGames.size(); j++) {
					String combineList = userGames.get(i) + " " + userGames.get(j);

					/*
					 * if(combineList.equals("A B"))
					 * System.out.println("Game from user A and B "+i+" "+j);
					 */

					jedis.hincrBy(COMBINE_MAP, combineList, 1);
				}
			}
		}

		//suggestionList(userGames);
	}

	// This function is used when user install new game.
	public void createCombinationWithOlderUesrGame(List<String> previousGames, List<String> newGames) {

		if (previousGames != null)
			Collections.sort(previousGames);

		if (newGames != null)
			Collections.sort(newGames);

		String combineList = null;
		if (previousGames != null && newGames != null) {
			for (String preGame : previousGames) {
				for (String newGame : newGames) {
					if (preGame.compareTo(newGame) < 0) {
						// userGame has less alphabatic order
						combineList = preGame + " " + newGame;
					} else
						combineList = newGame + " " + preGame;
					jedis.hincrBy(COMBINE_MAP, combineList, 1);
				}
			}
		}

		// call function to insert new Games
		insertNewList(newGames);
	}

	/*
	 * public void genrateCombinationForList(List<String> firstList,
	 * List<String> secondList) {
	 * System.out.println("Genrate "+firstList+" ---->"+secondList); Set<String>
	 * localInsert=new HashSet<String>(); for (int i = 0; i < firstList.size();
	 * i++) { for (int j = 0; j < secondList.size(); j++) { String gamesFromUser
	 * = firstList.get(i); String gamesFromRemaning = secondList.get(j); String
	 * combineList = null;
	 * 
	 * if((gamesFromRemaning.equals("A") && gamesFromUser.equals("B")) ||
	 * (gamesFromUser.equals("A") && gamesFromRemaning.equals("B")))
	 * System.out.println(i+" "+j+" this is value where i and j is a and b");
	 * 
	 * if (gamesFromUser.compareTo(gamesFromRemaning) < 0) { // userGame has
	 * less alphabatic order combineList = gamesFromUser + " " +
	 * gamesFromRemaning; if(!localInsert.contains(combineList)){
	 * localInsert.add(combineList); jedis.hincrBy(COMBINE_MAP, combineList, 1);
	 * }
	 * 
	 * } else if (gamesFromUser.compareTo(gamesFromRemaning) > 0) { combineList
	 * = gamesFromRemaning + " " + gamesFromUser;
	 * if(!localInsert.contains(combineList)){ localInsert.add(combineList);
	 * jedis.hincrBy(COMBINE_MAP, combineList, 1); } } } } }
	 */

	public void increaceDownloadCount(String packageName) {
		// System.out.println("Increace is call for " + packageName);
		jedis.hincrBy(DOWNLOAD_MAP, packageName, 1);
	}

	public List<String> suggestionList(List<String> userGame) {
		ArrayList<String> remaningGame = new ArrayList<String>(jedis.smembers(GAME_SET));
		Map<String, Double> suggesionMap = new HashMap<String, Double>();
		remaningGame.removeAll(userGame);

		for (int i = 0; i < userGame.size(); i++) {
			for (int j = 0; j < remaningGame.size(); j++) {
				int combineEfinity = 0;
				String gamesFromUser = userGame.get(i);
				String gamesFromRemaning = remaningGame.get(j);
				String combineList = null;
				try {
					if (gamesFromUser.compareTo(gamesFromRemaning) < 0) {
						// userGame has less alphabatic order
						combineList = gamesFromUser + " " + gamesFromRemaning;
						combineEfinity = Integer.parseInt(jedis.hget(COMBINE_MAP, combineList));
					} else {
						combineList = gamesFromRemaning + " " + gamesFromUser;
						combineEfinity = Integer.parseInt(jedis.hget(COMBINE_MAP, combineList));
					}
				} catch (NumberFormatException numberFormatException) {
					// System.out.println("exception for list is" +
					// combineList);
					log.error(numberFormatException + " for " + combineList);
					// numberFormatException.printStackTrace();
				} catch (Exception exception) {
					log.error(exception);
					exception.printStackTrace();
				}

				if (combineEfinity != 0) {
					try {
						double downloadForUserGame = Integer.parseInt(jedis.hget(DOWNLOAD_MAP, gamesFromUser));
						double downloadforAnother = Integer.parseInt(jedis.hget(DOWNLOAD_MAP, gamesFromRemaning));
						double probability = calculateProbability(combineEfinity, downloadForUserGame,
								downloadforAnother);

						// adding probabilility for calculating mean
						// probabilility
						if (suggesionMap.containsKey(gamesFromRemaning)) {
							double previous = suggesionMap.get(gamesFromRemaning);
							suggesionMap.put(gamesFromRemaning, previous + probability);
						} else
							suggesionMap.put(gamesFromRemaning, probability);

						System.out.println(combineEfinity + " Probabilility for game " + gamesFromUser + " ( "
								+ downloadForUserGame + " ) " + "  with  " + " ( " + downloadforAnother + " ) "
								+ gamesFromRemaning + " to download is " + probability);
					} catch (Exception exception) {
						exception.printStackTrace();
						log.error(exception);
					}
				}
			}
		}

		List<String> suggestionList = new ArrayList<String>();
		Iterator it = suggesionMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();

			if ((Double) pair.getValue() / remaningGame.size() > THRESHOLD) {
				suggestionList.add((String) pair.getKey());
			}
		}
		return suggestionList;
	}

	public double calculateProbability(double combineEfinity, double downloadForUserGame, double downloadforAnother) {
		return combineEfinity / (downloadForUserGame + downloadforAnother - combineEfinity);
	}

}
