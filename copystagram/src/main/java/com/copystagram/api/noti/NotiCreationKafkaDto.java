package com.copystagram.api.noti;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotiCreationKafkaDto {
	String ownerId;
	String content;
	LocalDateTime createdAt;
}
