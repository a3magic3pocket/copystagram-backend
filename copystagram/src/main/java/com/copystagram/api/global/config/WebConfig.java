package com.copystagram.api.global.config;

import java.time.Duration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
	public final GlobalConfig globalConfig;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		String rawOrigins = globalConfig.getCorsOrigins();
		String[] origins = rawOrigins.split(",");

		// @formatter:off
        registry.addMapping("/**")
	        .allowedOrigins(origins)
	        .allowedMethods("GET", "POST", "PUT", "DELETE")
	        .allowedHeaders("Authorization", "Content-Type")
	        .exposedHeaders("Custom-Header")
	        .allowCredentials(true)
	        .maxAge(3600);
        // @formatter:on
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/public/**").addResourceLocations("/public", "classpath:/static/")
				.setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));
	}
}
