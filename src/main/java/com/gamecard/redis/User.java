package com.gamecard.redis;

public class User implements DomainObject {

	public static final String OBJECT_KEY = "USER";

	public User() {
	}

	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}

	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + "]";
	}

	public String getKey() {
		return getId();
	}

	public String getObjectKey() {
		return OBJECT_KEY;
	}
}
