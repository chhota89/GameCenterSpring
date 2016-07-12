package com.gamecard.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.swing.text.StyledEditorKit.BoldAction;

import com.sun.org.apache.xpath.internal.operations.Bool;

@Entity
@Table(name="gameplaystore")
public class PlaystoreDto {
	@Id
	@GeneratedValue
	private int id;
	@Column
	private String gametittle;
	@Column
	private String category;
	@Column
	private String version;
	@Column
	private String Size;
	@Column
	private String gamedate;
	@Column
	private String packagename;
	@Column
	private String description; 
	@Column
	private boolean isgame;
	
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

}
