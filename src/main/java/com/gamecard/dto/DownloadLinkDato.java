package com.gamecard.dto;

import java.util.List;

public class DownloadLinkDato {
	private String vedioLink;
	
	private List<String> imageList;

	private String apkLink;
	
	
	
	public String getApkLink() {
		return apkLink;
	}
	public void setApkLink(String apkLink) {
		this.apkLink = apkLink;
	}
	public String getVedioLink() {
		return vedioLink;
	}
	public void setVedioLink(String vedioLink) {
		this.vedioLink = vedioLink;
	}
	public List<String> getImageList() {
		return imageList;
	}
	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}
	
	
}
