package com.copystagram.api.noticheck;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copystagram.api.global.dto.ErrorRespDto;
import com.copystagram.api.global.dto.SimpleSuccessRespDto;

import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
public class NotiCheckController {
	public final NotiCheckService notiCheckService;

	@PostMapping(value = "/noti-check")
	public ResponseEntity<?> create(Authentication authToken) {
		if (authToken == null) {
			ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "없는 유저 입니다");
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		this.notiCheckService.upsert(authToken.getName());

		SimpleSuccessRespDto simpleSuccessRespDto = new SimpleSuccessRespDto();
		simpleSuccessRespDto.setMessage("success");

		return new ResponseEntity<>(simpleSuccessRespDto, HttpStatus.OK);
	}
}
