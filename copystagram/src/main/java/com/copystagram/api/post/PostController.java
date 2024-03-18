package com.copystagram.api.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

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

	@PostMapping(value = "/post")
	public String create(@RequestParam(value = "description") @NotBlank @Max(1000) String desc,
			@RequestParam(value = "image") @NotBlank MultipartFile[] imageFiles, Authentication authToken)
			throws IOException {
		System.out.println("IN CREATE");

		PostCreationDto postCreationDto = new PostCreationDto();
		postCreationDto.setDescription(desc);

		Map<Integer, PostCreationImageDto> imageMap = new HashMap<Integer, PostCreationImageDto>();
		for (Integer i = 0; i < imageFiles.length; i++) {
			MultipartFile imageFile = imageFiles[i];
			PostCreationImageDto postCreationImageDto = new PostCreationImageDto();
			postCreationImageDto.setImageBytes(imageFile.getBytes());
			postCreationImageDto.setOriginalFilename(imageFile.getOriginalFilename());

			imageMap.put(i, postCreationImageDto);
		}
		postCreationDto.setImageMap(imageMap);
		postCreationDto.setOwnerId(authToken.getName());

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
