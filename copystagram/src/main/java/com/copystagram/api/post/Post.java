package com.copystagram.api.post;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Document(collection = MongodbCollectionName.POST)
@FieldNameConstants
@Getter
@Setter
public class Post {
	@Id
	public String _id;

	@Field(targetType = FieldType.OBJECT_ID)
	@NotNull(message = "ownerId is required")
	public String ownerId;
	
	@NotNull(message = "description is required")
	public String description;
	
	@NotNull(message = "imageDirName is required")
	public String imageDirName;
	
	@NotNull(message = "thumbImagePath is required")
	public String thumbImagePath;
	
	@NotNull(message = "contentImagePaths is required")
	public List<String> contentImagePaths;
	
	@NotNull(message = "docHash is required")
	public byte[] docHash;
	
	@NotNull(message = "createdAt is required")
	public LocalDateTime createdAt;
}
