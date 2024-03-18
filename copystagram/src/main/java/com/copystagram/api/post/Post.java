package com.copystagram.api.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "post")
@Getter
@Setter
public class Post {
	@Id
	public String _id;

	public String ownerId;
	public String description;
	public String imageDirName;
	public String thumbImagePath;
	public List<String> contentImagePaths;
	public byte[] docHash;
	public LocalDateTime createdAt;
}
