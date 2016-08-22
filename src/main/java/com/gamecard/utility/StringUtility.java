package com.gamecard.utility;

public class StringUtility {
	
	
	public static String compressImageUrl(String imageUrl,String ratio){
		return imageUrl.substring(0,imageUrl.length()-4)+ratio;
	}

}
