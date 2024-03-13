package com.copystagram.api.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "user")
@Getter
@Setter
public class User {
	@Id
	public String _id;

	@Indexed(unique = true)
	public String email;
	public String openId;
	public String name;
	public String locale;
	public String provider;
	public UserRole role;
	public Boolean isActive;
}
