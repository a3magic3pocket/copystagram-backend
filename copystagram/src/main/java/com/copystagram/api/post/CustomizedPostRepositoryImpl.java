package com.copystagram.api.post;

import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.bcel.Const;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
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
		SetOperation setOperation = SetOperation
				.set(PostRetrDto.Fields.ownerName)
				.toValue(ArrayElemAt.arrayOf(OWNER_INFO + '.' + User.Fields.name)
				.elementAt(0))
				;
 		// @formatter:on

		opsList.add(lookupOperation);
		opsList.add(setOperation);
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
		List<Criteria> criteriaList = List.of(Criteria.where(Post.Fields.ownerId).is(id));

		return this.getLatesPostsLogic(skip, limit, criteriaList);
	}
}
