package com.gamecard.dto;

import java.util.List;

public class DownloadLinkDato {
	public String vedioLink;
	public List<String> imageList;

	
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
