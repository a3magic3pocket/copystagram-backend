package com.copystagram.api.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRespDto {
	public String email;
	public String name;
	public String locale;
	public String description;
	public String userImagePath;
}
