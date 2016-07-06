package com.gamecard.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="gamedescription")
public class GameCardDto {
	@Id
	private int ganeid; 
	@Column
	private String gamepackagename;
	@Column
	private String gamename;
	@Column
	private String gamecategory;
	@Column
	private String gameverision;

	public int getGaneid() {
		return ganeid;
	}
	public void setGaneid(int ganeid) {
		this.ganeid = ganeid;
	}
	public String getGamepackagename() {
		return gamepackagename;
	}
	public void setGamepackagename(String gamepackagename) {
		this.gamepackagename = gamepackagename;
	}
	public String getGamename() {
		return gamename;
	}
	public void setGamename(String gamename) {
		this.gamename = gamename;
	}
	public String getGamecategory() {
		return gamecategory;
	}
	public void setGamecategory(String gamecategory) {
		this.gamecategory = gamecategory;
	}
	public String getGameverision() {
		return gameverision;
	}
	public void setGameverision(String gameverision) {
		this.gameverision = gameverision;
	}
	
}
