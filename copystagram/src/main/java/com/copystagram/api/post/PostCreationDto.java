package com.copystagram.api.post;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreationDto {
	String description;
	Map<Integer, PostCreationImageDto> imageMap;
}
