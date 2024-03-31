package com.copystagram.api.post;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListDto {
	public int pageNum;
	public int pageSize;
	public List<PostRetrDto> posts;
}
