package com.copystagram.api.streams;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostInfo {
	public String postId;
	public int numExposed;
	public int numLike;
	public int numReply;
}
