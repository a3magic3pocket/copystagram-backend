package com.copystagram.api.post;

import java.time.LocalDateTime;
import java.util.List;

import com.copystagram.api.user.User;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Getter
@Setter
public class PostRetrDto {
	public String ownerName;
	public String description;
	public String thumbImagePath;
	public List<String> contentImagePaths;
	public LocalDateTime createdAt;
}
