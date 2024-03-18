package com.copystagram.api.post;

import java.util.List;

public interface CustomizedPostRepository {
	public List<Post> getLatestList(int skip, int limit);
}
