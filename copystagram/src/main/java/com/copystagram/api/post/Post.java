package com.copystagram.api.post;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Post {
	@Id
	public Long id;
	
	public String title;
}
