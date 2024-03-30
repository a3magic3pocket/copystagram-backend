package com.copystagram.api.post;

import java.util.List;

public interface CustomizedPostRepository {
	public List<Post> getLatestAllPosts(int skip, int limit);

	public List<Post> getLatestPosts(int skip, int limit, String id);
}
