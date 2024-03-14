package com.copystagram.api.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.copystagram.api.global.config.GlobalConfig;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
public class AuthController {
	public final GlobalConfig globalConfig;

	@GetMapping("/auth/success")
	public ModelAndView authSucessful(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		Integer sessionMaxAge = session.getMaxInactiveInterval();

		// 힌트 쿠키 추가
		Cookie authHintCookie = new Cookie(globalConfig.getAuthHintCookieName(), "none");
		authHintCookie.setMaxAge(sessionMaxAge);
		authHintCookie.setPath("/");
		response.addCookie(authHintCookie);

		String fronendUri = globalConfig.getFrontendUri();

		return new ModelAndView("redirect:" + fronendUri);
	}

	@GetMapping("/auth/logout")
	public ModelAndView authLogout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		// 힌트 쿠키 제거
		Cookie authHintCookie = new Cookie(globalConfig.getAuthHintCookieName(), "none");
		authHintCookie.setMaxAge(0);
		authHintCookie.setPath("/");
		response.addCookie(authHintCookie);

		String fronendUri = globalConfig.getFrontendUri();

		return new ModelAndView("redirect:" + fronendUri);
	}
}
