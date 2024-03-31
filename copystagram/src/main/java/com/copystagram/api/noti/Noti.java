package com.copystagram.api.noti;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;

@Document(collection = MongodbCollectionName.NOTI)
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
	
	public String relatedPostId;
	public String redirectUrl;
	
	@NotNull(message = "docHash is required")
	public byte[] docHash;
	
	@NotNull(message = "docHash is required")
	public LocalDateTime createAt;
}
