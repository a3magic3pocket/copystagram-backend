package com.copystagram.api.like;

import com.mongodb.client.result.UpdateResult;

public interface CustomizedLikeRepository {
	public UpdateResult upsert(Like like);
}
