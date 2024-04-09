package com.copystagram.api.like;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeUpsertDto {
	public String postId;
	public String ownerId;
}
