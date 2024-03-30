package com.copystagram.api.post;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomizedPostRepositoryImpl implements CustomizedPostRepository {
	public final MongoTemplate mongoTemplate;

	private List<Post> getLatesPostsLogic(int skip, int limit, List<Criteria> criteriaList) {
		List<AggregationOperation> opsList = new ArrayList<>();
		for (Criteria criteria : criteriaList) {
			opsList.add(Aggregation.match(criteria));
		}
		opsList.add(Aggregation.sort(Sort.Direction.DESC, Post.Fields.createdAt));
		opsList.add(Aggregation.skip(skip));
		opsList.add(Aggregation.limit(limit));

		Aggregation aggregation = Aggregation.newAggregation(opsList);

		return mongoTemplate.aggregate(aggregation, MongodbCollectionName.POST, Post.class).getMappedResults();
	}

	@Override
	public List<Post> getLatestAllPosts(int skip, int limit) {

		return this.getLatesPostsLogic(skip, limit, List.of());
	}

	@Override
	public List<Post> getLatestPosts(int skip, int limit, String id) {
		List<Criteria> criteriaList = List.of(Criteria.where(Post.Fields.ownerId).is(id));

		return this.getLatesPostsLogic(skip, limit, criteriaList);
	}
}
