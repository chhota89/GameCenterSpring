package com.gamecard.daoimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class GameSuggestion {

	Jedis jedis = new Jedis("localhost");
	private static final String DOWNLOAD_MAP = "DOWNLOAD_MAP24";
	private static final String GAME_SET = "GAME_SET24";
	private static final String COMBINE_MAP = "COMBINE_MAP24";

	public void insertNewList(List<String> userGames) {
		Collections.sort(userGames);
		List<String> remaningGame = new ArrayList<String>(jedis.smembers(GAME_SET));
		
		jedis.sadd(GAME_SET, userGames.toArray(new String[userGames.size()]));


		for (String packageName : userGames)
			increaceDownloadCount(packageName);
		
		/*genrateCombinationForList(userGames, remaningGame);
		userGames.removeAll(remaningGame);*/

		
		if (userGames.size() != 0){
			//System.out.println("Game from user "+userGames);
			for (int i = 0; i < userGames.size() - 1; i++) {
				for (int j = i + 1; j < userGames.size(); j++) {
					String combineList = userGames.get(i) + " " + userGames.get(j);
					
					/*if(combineList.equals("A B"))
						System.out.println("Game from user A and B "+i+" "+j);*/
					
					jedis.hincrBy(COMBINE_MAP, combineList, 1);
				}
			}
		}
		
		suggestionList(userGames);
	}

	/*public void genrateCombinationForList(List<String> firstList, List<String> secondList) {
		System.out.println("Genrate "+firstList+" ---->"+secondList);
		Set<String> localInsert=new HashSet<String>();
		for (int i = 0; i < firstList.size(); i++) {
			for (int j = 0; j < secondList.size(); j++) {
				String gamesFromUser = firstList.get(i);
				String gamesFromRemaning = secondList.get(j);
				String combineList = null;
				
				if((gamesFromRemaning.equals("A") && gamesFromUser.equals("B")) || (gamesFromUser.equals("A") && gamesFromRemaning.equals("B")))
						System.out.println(i+" "+j+" this is value where i and j is a and b");
				
				if (gamesFromUser.compareTo(gamesFromRemaning) < 0) {
					// userGame has less alphabatic order
					combineList = gamesFromUser + " " + gamesFromRemaning;
					if(!localInsert.contains(combineList)){
						localInsert.add(combineList);
						jedis.hincrBy(COMBINE_MAP, combineList, 1);
					}

				} else if (gamesFromUser.compareTo(gamesFromRemaning) > 0) {
					combineList = gamesFromRemaning + " " + gamesFromUser;
					if(!localInsert.contains(combineList)){
						localInsert.add(combineList);
						jedis.hincrBy(COMBINE_MAP, combineList, 1);
					}
				}
			}
		}
	}*/

	public void increaceDownloadCount(String packageName) {
		//System.out.println("Increace is call for " + packageName);
		jedis.hincrBy(DOWNLOAD_MAP, packageName, 1);
	}

	public Map<String, Double> suggestionList(List<String> userGame) {
		ArrayList<String> remaningGame = new ArrayList<String>(jedis.smembers(GAME_SET));
		Map<String, Double> suggesionMap=new HashMap<String, Double>();
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
						combineList=gamesFromRemaning+" "+gamesFromUser;
						combineEfinity = Integer.parseInt(jedis.hget(COMBINE_MAP, combineList));
					}
				} catch (NumberFormatException numberFormatException) {
					System.out.println("exception for list is"+combineList);
					//numberFormatException.printStackTrace();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				try {
					double downloadForUserGame = Integer.parseInt(jedis.hget(DOWNLOAD_MAP, gamesFromUser));
					double downloadforAnother = Integer.parseInt(jedis.hget(DOWNLOAD_MAP, gamesFromRemaning));
					double probability = calculateProbability(combineEfinity,downloadForUserGame,downloadforAnother);
					
					if(probability>0.0)
						suggesionMap.put(gamesFromRemaning, probability);
					
					System.out.println(combineEfinity + " Probabilility for game " + gamesFromUser + " ( "
							+ downloadForUserGame + " ) " + "  with  " + " ( " + downloadforAnother + " ) "
							+ gamesFromRemaning + " to download is " + probability);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		System.out.println("----------------------------------------------------------------------");
		return suggesionMap;
	}
	
	public double calculateProbability(double combineEfinity,double downloadForUserGame,double downloadforAnother){
		return  combineEfinity / (downloadForUserGame + downloadforAnother - combineEfinity);
	}

}
