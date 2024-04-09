package com.copystagram.api.like;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Document(collection = MongodbCollectionName.LIKE)
@FieldNameConstants
@Setter
@Getter
public class Like {
	@Field(targetType = FieldType.OBJECT_ID)
	@NotNull(message = "postId is required")
	public String postId;

	@Field(targetType = FieldType.OBJECT_ID)
	@NotNull(message = "ownerId is required")
	public String ownerId;

	public Long numLikes;
}
