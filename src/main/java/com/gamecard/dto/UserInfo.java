package com.gamecard.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "user_info")
public class UserInfo {
	
	
	String userId;
	
	@Column
	String manufacturer;
	
	@Column
	int androidVersion;
	
	List<PlaystoreDto> playstoreDtos=new ArrayList();

	@Id
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "manufacturer")
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Column(name = "android_version")
	public int getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion(int androidVersion) {
		this.androidVersion = androidVersion;
	}

	@ManyToMany(fetch = FetchType.LAZY ,cascade = {CascadeType.PERSIST ,CascadeType.ALL})
	public List<PlaystoreDto> getPlaystoreDtos() {
		return playstoreDtos;
	}

	public void setPlaystoreDtos(List<PlaystoreDto> playstoreDtos) {
		this.playstoreDtos = playstoreDtos;
	}
	
	

}
