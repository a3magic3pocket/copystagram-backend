package com.copystagram.api.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
	public String ownerId;
	public String description;
	public String imageDirName;
	public String thumbImagePath;
	public List<String> contentImagePaths;
	public byte[] docHash;
	public LocalDateTime createdAt;
}
