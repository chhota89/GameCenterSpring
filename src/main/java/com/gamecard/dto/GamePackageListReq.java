package com.gamecard.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "manufacturer", "version", "packageList", "topic" })
public class GamePackageListReq {

	@JsonProperty("manufacturer")
	private String manufacturer;
	@JsonProperty("version")
	private Integer version;
	@JsonProperty("packageList")
	private List<String> packageList = new ArrayList<String>();
	@JsonProperty("topic")
	private String topic;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The manufacturer
	 */
	@JsonProperty("manufacturer")
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * 
	 * @param manufacturer
	 *            The manufacturer
	 */
	@JsonProperty("manufacturer")
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	/**
	 * 
	 * @return The version
	 */
	@JsonProperty("version")
	public Integer getVersion() {
		return version;
	}

	/**
	 * 
	 * @param version
	 *            The version
	 */
	@JsonProperty("version")
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * 
	 * @return The packageList
	 */
	@JsonProperty("packageList")
	public List<String> getPackageList() {
		return packageList;
	}

	/**
	 * 
	 * @param packageList
	 *            The packageList
	 */
	@JsonProperty("packageList")
	public void setPackageList(List<String> packageList) {
		this.packageList = packageList;
	}

	/**
	 * 
	 * @return The topic
	 */
	@JsonProperty("topic")
	public String getTopic() {
		return topic;
	}

	/**
	 * 
	 * @param topic
	 *            The topic
	 */
	@JsonProperty("topic")
	public void setTopic(String topic) {
		this.topic = topic;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}