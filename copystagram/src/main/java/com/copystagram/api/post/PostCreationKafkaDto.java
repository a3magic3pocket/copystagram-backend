package com.copystagram.api.post;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreationKafkaDto {
	String description;
	String imageDirName;
	String ownerId;
	LocalDateTime createdAt;
}
