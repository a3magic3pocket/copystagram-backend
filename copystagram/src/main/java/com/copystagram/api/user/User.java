package com.copystagram.api.user;

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

	@Indexed(unique = true)
	public String email;
	public String openId;
	public String name;
	public String locale;
	public String provider;
	public String description;
	public String userImagePath;
	public UserRole role;
	public Boolean isActive;
}
