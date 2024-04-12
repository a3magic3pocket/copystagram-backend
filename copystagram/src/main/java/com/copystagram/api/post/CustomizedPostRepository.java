package com.copystagram.api.post;

import java.util.List;

public interface CustomizedPostRepository {
	public List<PostRetrDto> getLatestAllPosts(int skip, int limit);

	public List<PostRetrDto> getLatestPosts(int skip, int limit, String id);

	public List<PostRetrDto> getPopularAllPosts(int skip, int limit);

	public List<PostRetrDto> getRelatedAllPosts(int skip, int limit, String id);

	public PostCountDto countPostsById(String id);
}
