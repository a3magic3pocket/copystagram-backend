package com.copystagram.api.user;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copystagram.api.global.dto.ErrorRespDto;

import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
public class UserController {
	public final UserRepository userRepository;

	@GetMapping("/user-info/me")
	public ResponseEntity<?> retrieveMyUser(Authentication authToken) {
		ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "없는 유저 입니다");
		if (authToken == null) {
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		Optional<User> optUser = this.userRepository.findById(authToken.getName());

		if (optUser.isPresent()) {
			User user = optUser.get();

			UserRespDto userRespDto = new UserRespDto();
			userRespDto.setEmail(user.getEmail());
			userRespDto.setName(user.getName());
			userRespDto.setLocale(user.getLocale());
			userRespDto.setDescription(user.getDescription());

			return new ResponseEntity<>(userRespDto, HttpStatus.OK);
		}

		return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
