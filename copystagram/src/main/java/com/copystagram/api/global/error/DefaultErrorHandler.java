package com.copystagram.api.global.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.copystagram.api.global.dto.ErrorRespDto;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class DefaultErrorHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleContraintViolationException(ConstraintViolationException e) {
		String errorMessage = e.getMessage();

		String[] splitted = e.getMessage().split(" ");

		boolean isColonContained = splitted[0].contains(":");
		if (isColonContained && splitted.length > 1) {
			List<String> removed = new ArrayList<String>();
			for (int i = 1; i < splitted.length; i++) {
				removed.add(splitted[i]);
			}
			errorMessage = String.join(" ", removed);

		}

		ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", errorMessage);
		return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
		String errorMessage = e.getRequestPartName() + " 를 추가해주세요.";
		ErrorRespDto errorRespDto = new ErrorRespDto("9999", "ko", errorMessage);

		return new ResponseEntity<>(errorRespDto, HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
