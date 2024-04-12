package com.copystagram.api.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.copystagram.api.global.dto.ErrorRespDto;
import com.copystagram.api.global.dto.SimpleSuccessRespDto;
import com.copystagram.api.metapostlist.MetaPostListService;
import com.mongodb.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
@Validated
public class PostController {
	public final PostService postService;
	public final PostRepository postRepository;
	public final MetaPostListService metaPostListService;

	@PostMapping(value = "/post")
	public String create(
			@Valid @NotBlank(message = "문구를 입력주세요.") @Size(max = 1000, message = "문구는 1000자 이하만 입력할 수 있습니다.") @RequestParam(value = "description") String desc,
			@Valid @NotNull(message = "이미지 파일을 1개 이상 추가해야 합니다.") @RequestParam(value = "image") MultipartFile[] imageFiles,
			Authentication authToken) throws IOException {

		PostCreationDto postCreationDto = new PostCreationDto();
		postCreationDto.setDescription(desc);

		Map<Integer, PostCreationImageDto> imageMap = new HashMap<Integer, PostCreationImageDto>();
		for (Integer i = 0; i < imageFiles.length; i++) {
			MultipartFile imageFile = imageFiles[i];
			PostCreationImageDto postCreationImageDto = new PostCreationImageDto();
			postCreationImageDto.setImageBytes(imageFile.getBytes());
			postCreationImageDto.setOriginalFilename(imageFile.getOriginalFilename());
			System.out.println(i + "   " + imageFile.getOriginalFilename());

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

		return postService.getPopularAllPosts(pageNum, pageSize);
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

	@GetMapping("/related-posts")
	public ResponseEntity<?> listRelatedPosts(
			@RequestParam(value = "page-num", required = false) @Nullable Integer pageNum,
			@RequestParam(value = "hook-post-id", required = false) @Nullable String hookPostId,
			Authentication authToken) {
		if (authToken == null) {
			ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "없는 유저 입니다");
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		if (hookPostId == null) {
			ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "hook-post-id 오류");
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		boolean exists = this.postRepository.existsById(hookPostId);
		if (!exists) {
			ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "hook-post-id 오류");
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		int pageSize = 9;
		if (pageNum == null || pageNum <= 0) {
			pageNum = 1;
		}
		System.out.println("hookPostId+++" + hookPostId);

		PostListDto posts = postService.getRelatedAllPosts(pageNum, pageSize, hookPostId);

		return new ResponseEntity<>(posts, HttpStatus.OK);
	}

	@GetMapping("/my-posts/count")
	public ResponseEntity<?> countMyPosts(Authentication authToken) {
		if (authToken == null) {
			ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "없는 유저 입니다");
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		PostCountDto postCountDto = postService.countPostsById(authToken.getName());

		return new ResponseEntity<>(postCountDto, HttpStatus.OK);
	}

	@PostMapping("/post/click-count")
	public ResponseEntity<?> countPostListNumClicks(
			@Valid @NotBlank(message = "postId 값이 없습니다.") @RequestParam(value = "postId") String postId) {
		boolean isOk = metaPostListService.countNumClicks(postId);
		if (isOk) {
			SimpleSuccessRespDto simpleSuccessRespDto = new SimpleSuccessRespDto();
			simpleSuccessRespDto.setMessage("success");

			return new ResponseEntity<>(simpleSuccessRespDto, HttpStatus.OK);
		}

		ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "처리 실패");
		return new ResponseEntity<>(errorRespDto, HttpStatus.BAD_REQUEST);
	}
}
