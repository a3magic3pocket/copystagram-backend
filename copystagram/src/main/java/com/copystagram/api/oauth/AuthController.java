package com.copystagram.api.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
public class AuthController {
//	@GetMapping("/auth/success")
//	public ModelAndView authSucessful() {
//		return new ModelAndView("redirect:https://google.com");
//	}

	@GetMapping("/auth/success")
	public String authSucessful() {
		return "success";
	}

	@GetMapping("/auth/logout")
	public String authLogout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		return "logout";
	}
}
