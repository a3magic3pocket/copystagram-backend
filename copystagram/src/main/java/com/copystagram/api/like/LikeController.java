package com.copystagram.api.like;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.copystagram.api.global.dto.ErrorRespDto;
import com.copystagram.api.global.dto.SimpleSuccessRespDto;
import com.copystagram.api.post.Post;
import com.copystagram.api.post.PostRepository;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
@Validated
public class LikeController {
	public final PostRepository postRepository;
	public final LikeService likeService;

	private ErrorRespDto isPostIdValid(String inputPostId, String userId, Optional<Post> optPost) {
		if (optPost.isEmpty()) {
			return new ErrorRespDto("9999", "ko", "잘못된 postId 입니다");
		}

		return null;
	}

	@GetMapping(value = "/like/up")
	public ResponseEntity<?> up(@Valid @NotNull(message = "hello world") @RequestParam(value = "postId") String postId,
			Authentication authToken) {
		Optional<Post> optPost = postRepository.findById(postId);

		ErrorRespDto errorRespDto = this.isPostIdValid(postId, authToken.getName(), optPost);
		if (errorRespDto != null) {
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		LikeUpsertDto likeUpsertDto = new LikeUpsertDto();
		likeUpsertDto.setPostId(postId);
		likeUpsertDto.setOwnerId(authToken.getName());

		this.likeService.up(likeUpsertDto);

		SimpleSuccessRespDto simpleSuccessRespDto = new SimpleSuccessRespDto();
		simpleSuccessRespDto.setMessage("success");

		return new ResponseEntity<>(simpleSuccessRespDto, HttpStatus.OK);
	}

	@GetMapping(value = "/like/down")
	public ResponseEntity<?> down(@NotEmpty(message = "postId는 필수값입니다") @RequestParam("postId") String postId,
			Authentication authToken) {
		Optional<Post> optPost = postRepository.findById(postId);

		ErrorRespDto errorRespDto = this.isPostIdValid(postId, authToken.getName(), optPost);
		if (errorRespDto != null) {
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		LikeUpsertDto likeUpsertDto = new LikeUpsertDto();
		likeUpsertDto.setPostId(postId);
		likeUpsertDto.setOwnerId(authToken.getName());

		this.likeService.down(likeUpsertDto);

		SimpleSuccessRespDto simpleSuccessRespDto = new SimpleSuccessRespDto();
		simpleSuccessRespDto.setMessage("success");

		return new ResponseEntity<>(simpleSuccessRespDto, HttpStatus.OK);
	}
}
