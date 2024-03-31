package com.copystagram.api.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.copystagram.api.global.dto.ErrorRespDto;
import com.mongodb.lang.Nullable;

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
			System.out.println(i +"   " + imageFile.getOriginalFilename());

			imageMap.put(i, postCreationImageDto);
		}
		postCreationDto.setImageMap(imageMap);
		postCreationDto.setOwnerId(authToken.getName());

		postService.create(postCreationDto);

		return "success";
	}

	@GetMapping("/posts")
	public PostListDto list(@RequestParam(value = "page-num", required = false) @Nullable Integer pageNum) {
		int pageSize = 9;
		if (pageNum == null || pageNum <= 0) {
			pageNum = 1;
		}

		return postService.getLatestAllPosts(pageNum, pageSize);
	}

	@GetMapping("/my-posts")
	public ResponseEntity<?> listMyPosts(@RequestParam(value = "page-num", required = false) @Nullable Integer pageNum,
			Authentication authToken) {
		if (authToken == null) {
			ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "없는 유저 입니다");
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		int pageSize = 9;
		if (pageNum == null || pageNum <= 0) {
			pageNum = 1;
		}

		PostListDto posts = postService.getLatestPosts(pageNum, pageSize, authToken.getName());

		return new ResponseEntity<>(posts, HttpStatus.OK);
	}
}
