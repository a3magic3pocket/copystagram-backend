package com.copystagram.api.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreationImageDto {
	byte[] imageBytes;
	String originalFilename;
}
