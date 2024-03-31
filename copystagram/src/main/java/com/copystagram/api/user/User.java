package com.copystagram.api.user;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Document(collection = MongodbCollectionName.USER)
@FieldNameConstants
@Getter
@Setter
public class User {
	@Id
	public String _id;

	
	@NotNull(message = "email is required")
	public String email;
	
	@NotNull(message = "openId is required")
	public String openId;
	
	@Indexed(unique = true)
	@NotNull(message = "name is required")
	public String name;
	
	@NotNull(message = "provider is required")
	public String provider;
	
	@NotNull(message = "role is required")
	public UserRole role;
	
	@NotNull(message = "isActive is required")
	public Boolean isActive;
	
	public String locale;
	public String description;
	public String userImagePath;
}
