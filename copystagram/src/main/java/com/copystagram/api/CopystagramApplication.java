package com.copystagram.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:copystagram.properties")
@ConfigurationPropertiesScan
public class CopystagramApplication {

	public static void main(String[] args) {
		SpringApplication.run(CopystagramApplication.class, args);
	}

}
