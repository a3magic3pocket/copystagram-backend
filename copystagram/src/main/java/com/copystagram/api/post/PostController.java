package com.copystagram.api.post;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
public class PostController {

	@GetMapping("/posts")
	public String list(Authentication auth) {
		Object a = auth.getPrincipal();
		System.out.println(a);
		return "posts";
	}

	@GetMapping("/post/{id}")
	public String retrieve() {
		return "retrieve";
	}
}
