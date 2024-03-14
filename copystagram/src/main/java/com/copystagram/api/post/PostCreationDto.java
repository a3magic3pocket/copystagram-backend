package com.copystagram.api.post;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreationDto {
	String description;
	MultipartFile[] imageFiles;
}
