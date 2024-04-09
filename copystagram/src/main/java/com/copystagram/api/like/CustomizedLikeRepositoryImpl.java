package com.copystagram.api.like;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.UpdateResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomizedLikeRepositoryImpl implements CustomizedLikeRepository {
	public final MongoTemplate mongoTemplate;

//	@Override
	public UpdateResult upsert(Like like) {
		Query query = new Query();
		Criteria criteria = new Criteria();

		// @formatter:off
		criteria.andOperator(
			Criteria.where(Like.Fields.ownerId).is(like.getOwnerId()),
			Criteria.where(Like.Fields.postId).is(like.getPostId())
		);
		// @formatter:one
		query.addCriteria(criteria);

		Update update = new Update();
		update.set(Like.Fields.numLikes, like.getNumLikes());

		return this.mongoTemplate.upsert(query, update, Like.class);
	}
}
