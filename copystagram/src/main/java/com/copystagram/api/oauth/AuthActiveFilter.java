package com.copystagram.api.oauth;

import java.io.IOException;
import java.security.Principal;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthActiveFilter implements Filter {
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletResponse resp = (HttpServletResponse) servletResponse;
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) req.getUserPrincipal();

		boolean isActive = authToken.getPrincipal().getAttribute(OAuth2UserKey.IS_ACTIVE.getValue());
		boolean isAuthenticated = authToken.isAuthenticated();

		if (isAuthenticated && !isActive) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "your account has been banned");
			return;
		}

		filterChain.doFilter(servletRequest, servletResponse);

	}

}
