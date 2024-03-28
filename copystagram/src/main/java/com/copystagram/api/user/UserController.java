package com.copystagram.api.user;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copystagram.api.global.dto.ErrorRespDto;

import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/v1")
@RequiredArgsConstructor
@RestController
public class UserController {
	public final UserRepository userRepository;

	@GetMapping("/my-user-info")
	public ResponseEntity<?> retrieveMyUser(Authentication authToken) {
		ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "없는 유저 입니다");
		if (authToken == null) {
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		Optional<User> optUser = this.userRepository.findById(authToken.getName());
		if (optUser.isEmpty()) {
			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		User user = optUser.get();

		UserRespDto userRespDto = new UserRespDto();
		userRespDto.setEmail(user.getEmail());
		userRespDto.setName(user.getName());
		userRespDto.setLocale(user.getLocale());
		userRespDto.setDescription(user.getDescription());

		return new ResponseEntity<>(userRespDto, HttpStatus.OK);
	}

	@GetMapping("/user-info/{id}")
	public ResponseEntity<?> retrieve(@PathVariable(value = "id") @NotNull String name) {
		User user = this.userRepository.findByName(name);
		if (user == null) {
			ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", "없는 유저 입니다");

			return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		UserRespDto userRespDto = new UserRespDto();
		userRespDto.setEmail(user.getEmail());
		userRespDto.setName(user.getName());
		userRespDto.setLocale(user.getLocale());
		userRespDto.setDescription(user.getDescription());
		userRespDto.setUserImagePath(user.getUserImagePath());

		return new ResponseEntity<>(userRespDto, HttpStatus.OK);
	}
}
