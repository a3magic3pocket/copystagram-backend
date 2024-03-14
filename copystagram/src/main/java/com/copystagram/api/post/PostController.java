package com.copystagram.api.post;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
public class PostController {

	public final PostService postService;
	public final KafkaTemplate<String, Object> exptKafkaTemplate;

	@PostMapping(value = "/post")
	public String create(@RequestParam(value = "description") @NotBlank @Max(1000) String desc,
			@RequestParam(value = "image") @NotBlank MultipartFile[] imageFiles) {
		System.out.println("IN CREATE");

		System.out.println("file: " + imageFiles);
		for (int i = 0; i < imageFiles.length; i++) {
			System.out.println("imageFiles[i]" + imageFiles[i]);
		}

		System.out.println("desc: " + desc);
		PostCreationDto postCreationDto = new PostCreationDto();
		postCreationDto.setDescription(desc);
		postCreationDto.setImageFiles(imageFiles);

		postService.create(postCreationDto);

		return "create";
	}

	@GetMapping("/posts")
	public String list(Authentication auth) {
		Object a = auth.getPrincipal();
		System.out.println(a);
		return "posts";
	}

}
