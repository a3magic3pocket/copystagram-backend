package com.copystagram.api.oauth;

public enum OAuth2UserKey {
	OPEN_ID("openId"), EMAIL("email"), IS_ACTIVE("isActive");

	private String value;

	private OAuth2UserKey(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
