package com.gamecard.redis;

import java.io.Serializable;

public interface DomainObject extends Serializable{
	
	 String getKey();
	 
	 String getObjectKey();

}
