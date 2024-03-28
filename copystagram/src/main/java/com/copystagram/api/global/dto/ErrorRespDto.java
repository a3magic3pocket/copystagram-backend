package com.copystagram.api.global.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorRespDto {
	public String code;
	public String locale;
	public String message;
	
	public ErrorRespDto(String code, String locale, String message) {
		this.code = code; 
		this.locale = locale;  
		this.message = message; 
	}
}
