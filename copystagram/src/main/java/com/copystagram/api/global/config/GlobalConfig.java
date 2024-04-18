package com.copystagram.api.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("copystagram")
@Setter
@Getter
public class GlobalConfig {
	private String frontendUri;
	private String corsOrigins;
	private String authHintCookieName;
	private String staticDirPath;
	private String rootImageDirName;
	private String rawDirName;
	private String thumbDirName;
	private String contentDirName;
}
