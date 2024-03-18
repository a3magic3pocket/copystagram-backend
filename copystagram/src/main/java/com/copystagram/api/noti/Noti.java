package com.copystagram.api.noti;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "noti")
@Getter
@Setter
public class Noti {
	@Id
	public String _id;

	public String ownerId;
	public String content;
	public byte[] docHash;
	public LocalDateTime createAt;
}
