package com.copystagram.api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.copystagram.api.oauth.AuthActiveFilter;
import com.copystagram.api.user.UserRole;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityGroup {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// @formatter:off
        http
        	.csrf(AbstractHttpConfigurer::disable)
        	.addFilterAfter(new AuthActiveFilter(), AuthorizationFilter.class)
        	.authorizeHttpRequests((a) -> a
        					.requestMatchers("/v1/my-user-info").authenticated()
        					.requestMatchers("/v1/my-posts").authenticated()
                            .requestMatchers("/v1/my-posts/count").authenticated()
                            .requestMatchers("/v1/posts").authenticated()
                            .requestMatchers("/v1/my-notifications").authenticated()
                            .requestMatchers("/v1/auth/logout").authenticated()
//                            .requestMatchers("/v1/post/**").hasRole(UserRole.NORMAL.toString())
                            .anyRequest().permitAll()
            )
            .exceptionHandling(e -> e
                            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .oauth2Login(l -> l
            		.defaultSuccessUrl("/v1/auth/success")
            )
            .logout(l -> l
            		.logoutSuccessUrl("/v1/auth/logout")
            )
            ;
        // @formatter:on

		return http.build();
	}
}
