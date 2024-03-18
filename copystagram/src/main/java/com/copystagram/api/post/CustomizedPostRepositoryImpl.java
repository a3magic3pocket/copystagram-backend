package com.copystagram.api.post;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomizedPostRepositoryImpl implements CustomizedPostRepository {
	public final MongoTemplate mongoTemplate;

	@Override
	public List<Post> getLatestList(int skip, int limit) {

		// @formatter:off
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.sort(Sort.Direction.DESC, Post.Fields.createdAt),
				Aggregation.skip(skip),
				Aggregation.limit(limit)
			);
		// @formatter:on

		return mongoTemplate.aggregate(aggregation, MongodbCollectionName.POST, Post.class).getMappedResults();
	}
}
