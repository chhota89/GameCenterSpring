package com.gamecard.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
@Table(name = "gameplaystore")
public class PlaystoreDto {

	/*@SerializedName("id")
	@Expose*/
	@Id
	@GeneratedValue
	private int id;
	@SerializedName("gametittle")
	@Expose
	@Column
	private String gametittle;
	@SerializedName("category")
	@Expose
	@Column
	private String category;
	@SerializedName("version")
	@Expose
	@Column
	private String version;
	@SerializedName("Size")
	@Expose
	@Column
	private String Size;
	@SerializedName("gamedate")
	@Expose
	@Column
	private String gamedate;
	@SerializedName("packagename")
	@Expose
	@Column
	private String packagename;
	@SerializedName("description")
	@Expose
	@Column(length=20000)
	private String description;
	@SerializedName("isgame")
	@Expose
	@Column
	private boolean isgame;
	
	@SerializedName("iconLink")
	@Expose
	@Column
	private String iconLink;
	
	public String getIconLink() {
		return iconLink;
	}

	public void setIconLink(String iconLink) {
		this.iconLink = iconLink;
	}

	@SerializedName("jsonImageVedioLink")
	@Expose
	@Column(length=20000)
	private String jsonImageVedioLink;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGametittle() {
		return gametittle;
	}

	public void setGametittle(String gametittle) {
		this.gametittle = gametittle;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
	}

	public String getGamedate() {
		return gamedate;
	}

	public void setGamedate(String gamedate) {
		this.gamedate = gamedate;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getIsgame() {
		return isgame;
	}

	public void setIsgame(Boolean isgame) {
		this.isgame = isgame;
	}

	public String getJsonImageVedioLink() {
		return jsonImageVedioLink;
	}

	public void setJsonImageVedioLink(String jsonImageVedioLink) {
		this.jsonImageVedioLink = jsonImageVedioLink;
	}
	
}
