package com.copystagram.api.noti;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.copystagram.api.global.dto.ErrorRespDto;
import com.copystagram.api.noticheck.NotiCheckService;
import com.mongodb.lang.Nullable;

import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
public class NotiController {
	public final NotiService notiService;
	public final NotiCheckService notiCheckService;

	@GetMapping("/my-notifications")
	public ResponseEntity<?> listMyNotis(@RequestParam(value = "page-num", required = false) @Nullable Integer pageNum,
			Authentication authToken) {
		if (authToken == null) {
			ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "없는 유저 입니다");
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		int pageSize = 9;
		if (pageNum == null || pageNum <= 0) {
			pageNum = 1;
		}

		NotiListDto notis = this.notiService.getLatestNotis(pageNum, pageSize, authToken.getName());

		return new ResponseEntity<>(notis, HttpStatus.OK);
	}

	@GetMapping("/my-notifications/unchecked")
	public ResponseEntity<?> listMyUncheckedPosts(Authentication authToken) {
		if (authToken == null) {
			ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "없는 유저 입니다");
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		int pageSize = 9;

		List<String> notiIds = this.notiService.getMyUncheckedNotis(1, pageSize, authToken.getName());

		return new ResponseEntity<>(notiIds, HttpStatus.OK);

	}

}
