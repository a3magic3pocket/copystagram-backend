package com.copystagram.api.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("copystagram")
@Setter
@Getter
public class GlobalConfig {
	private String frontendUri;
	private String authHintCookieName;
}
