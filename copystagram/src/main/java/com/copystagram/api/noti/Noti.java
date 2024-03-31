package com.copystagram.api.noti;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.Field.Write;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Document(collection = MongodbCollectionName.NOTI)
@FieldNameConstants
@Getter
@Setter
public class Noti {
	@Id
	public String _id;

	@Field(targetType = FieldType.OBJECT_ID)
	@NotNull(message = "ownerId is required")
	public String ownerId;

	@NotNull(message = "content is required")
	public String content;

	@Field(targetType = FieldType.OBJECT_ID, write = Write.ALWAYS)
	public String relatedPostId;

	@NotNull(message = "docHash is required")
	public byte[] docHash;

	@NotNull(message = "createdAt is required")
	public LocalDateTime createdAt;
}
