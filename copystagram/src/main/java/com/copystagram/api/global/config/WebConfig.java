package com.copystagram.api.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// @formatter:off
        registry.addMapping("/**")
	        .allowedOrigins("http://localhost:3000")
	        .allowedMethods("GET", "POST", "PUT", "DELETE")
	        .allowedHeaders("Authorization", "Content-Type")
	        .exposedHeaders("Custom-Header")
	        .allowCredentials(true)
	        .maxAge(3600);
        // @formatter:on
	}

}
