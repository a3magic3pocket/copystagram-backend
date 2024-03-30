package com.copystagram.api.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Document(collection = MongodbCollectionName.POST)
@Getter
@Setter
public class Post {
	@Id
	public String _id;

	public String ownerId;
	public String ownerName;
	public String description;
	public String imageDirName;
	public String thumbImagePath;
	public List<String> contentImagePaths;
	public byte[] docHash;
	public LocalDateTime createdAt;
}
