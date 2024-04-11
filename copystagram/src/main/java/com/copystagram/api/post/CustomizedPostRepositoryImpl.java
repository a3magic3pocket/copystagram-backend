package com.copystagram.api.post;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.copystagram.api.global.config.MongodbCollectionName;
import com.copystagram.api.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomizedPostRepositoryImpl implements CustomizedPostRepository {
	public final MongoTemplate mongoTemplate;

	private List<PostRetrDto> getLatesPostsLogic(int skip, int limit, List<Criteria> criteriaList) {
		List<AggregationOperation> opsList = new ArrayList<>();
		final String OWNER_INFO = "ownerInfo";

		for (Criteria criteria : criteriaList) {
			opsList.add(Aggregation.match(criteria));
		}

		// @formatter:off
 		LookupOperation lookupOperation = LookupOperation.newLookup()
 				.from(MongodbCollectionName.USER)
 				.localField(Post.Fields.ownerId)
 				.foreignField(User.Fields._id)
 				.as(OWNER_INFO)
 				;
		SetOperation setOwnerNameOperation = SetOperation
				.set(PostRetrDto.Fields.ownerName)
				.toValue(
					ArrayElemAt.arrayOf(OWNER_INFO + '.' + User.Fields.name).elementAt(0)
				)
				;
		
		SetOperation setPostIdOperation = SetOperation
				.set(PostRetrDto.Fields.postId)
				.toValue("$" + Post.Fields._id)
				;
 		// @formatter:on

		opsList.add(lookupOperation);
		opsList.add(setOwnerNameOperation);
		opsList.add(setPostIdOperation);
		opsList.add(Aggregation.sort(Sort.Direction.DESC, Post.Fields.createdAt));
		opsList.add(Aggregation.skip(skip));
		opsList.add(Aggregation.limit(limit));

		Aggregation aggregation = Aggregation.newAggregation(opsList);

		return mongoTemplate.aggregate(aggregation, MongodbCollectionName.POST, PostRetrDto.class).getMappedResults();
	}

	@Override
	public List<PostRetrDto> getLatestAllPosts(int skip, int limit) {

		return this.getLatesPostsLogic(skip, limit, List.of());
	}

	@Override
	public List<PostRetrDto> getLatestPosts(int skip, int limit, String id) {
		List<Criteria> criteriaList = List.of(Criteria.where(Post.Fields.ownerId).is(new ObjectId(id)));

		return this.getLatesPostsLogic(skip, limit, criteriaList);
	}

	@Override
	public PostCountDto countPostsById(String id) {
		Criteria criteria = Criteria.where(Post.Fields.ownerId).is(new ObjectId(id));

		List<AggregationOperation> opsList = new ArrayList<>();
		opsList.add(Aggregation.match(criteria));
		opsList.add(Aggregation.count().as("count"));

		Aggregation aggregation = Aggregation.newAggregation(opsList);

		return mongoTemplate.aggregate(aggregation, MongodbCollectionName.POST, PostCountDto.class).getMappedResults()
				.getFirst();
	}
}
