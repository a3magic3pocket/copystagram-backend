package com.copystagram.api.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreationKafkaDto {
	String description;
	String imageDirName;
	String ownerId;
}
