package com.copystagram.api.post;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.SetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.copystagram.api.global.config.MongodbCollectionName;
import com.copystagram.api.like.Like;
import com.copystagram.api.metapost.MetaPost;
import com.copystagram.api.metapostlist.MetaPostList;
import com.copystagram.api.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomizedPostRepositoryImpl implements CustomizedPostRepository {
	public final MongoTemplate mongoTemplate;

	private List<PostRetrDto> getLatesPostsLogic(int skip, int limit, List<Criteria> criteriaList) {
		List<AggregationOperation> opsList = new ArrayList<>();
		final String OWNER_INFO = "ownerInfo";
		final String LIKE_INFO = "likeInfo";

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
		SetOperation setNumLikesOperation = SetOperation
				.set(PostRetrDto.Fields.numLikes)
				.toValue(
					ArrayElemAt.arrayOf(LIKE_INFO + '.' + Like.Fields.numLikes).elementAt(0)
				)
				;
 		// @formatter:on

		opsList.add(lookupOperation);
		opsList.add(setOwnerNameOperation);
		opsList.add(setPostIdOperation);
		opsList.add(setNumLikesOperation);
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

	@Override
	public List<PostRetrDto> getPopularAllPosts(int skip, int limit) {
		List<AggregationOperation> opsList = new ArrayList<>();
		final String OWNER_INFO = "ownerInfo";
		final String META_POST_LIST_INFO = "metaPostListInfo";
		final String LIKE_INFO = "likeInfo";
		final String POPULAR_INDEX = "popularIndex";

		// @formatter:off
 		LookupOperation lookupOwnerInfoOperation = LookupOperation.newLookup()
 				.from(MongodbCollectionName.USER)
 				.localField(Post.Fields.ownerId)
 				.foreignField(User.Fields._id)
 				.as(OWNER_INFO)
 				;
 		LookupOperation lookupMetaPostListInfoOperation = LookupOperation.newLookup()
 				.from(MongodbCollectionName.META_POST_LIST)
 				.localField(Post.Fields._id)
 				.foreignField(MetaPostList.Fields.postId)
 				.as(META_POST_LIST_INFO)
 				;
 		LookupOperation lookupLikeInfoOperation = LookupOperation.newLookup()
 				.from(MongodbCollectionName.LIKE)
 				.localField(Post.Fields._id)
 				.foreignField(Like.Fields.postId)
 				.as(LIKE_INFO)
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
		SetOperation setNumLikesOperation = SetOperation
				.set(PostRetrDto.Fields.numLikes)
				.toValue(
					ArrayElemAt.arrayOf(LIKE_INFO + '.' + Like.Fields.numLikes).elementAt(0)
				)
				;
		SetOperation setPopularIndexOperation = SetOperation
				.set(POPULAR_INDEX)
				.toValue(
					ArithmeticOperators.Divide.valueOf(
						ArrayElemAt.arrayOf(META_POST_LIST_INFO + '.' + MetaPostList.Fields.numClicks).elementAt(0)
					).divideBy(
						ArithmeticOperators.Add.valueOf(
							ArrayElemAt.arrayOf(META_POST_LIST_INFO + '.' + MetaPostList.Fields.numViews).elementAt(0)
						).add(0.00000000001)
					)
				)
				;
 		// @formatter:on

		opsList.add(lookupOwnerInfoOperation);
		opsList.add(lookupMetaPostListInfoOperation);
		opsList.add(lookupLikeInfoOperation);
		opsList.add(setOwnerNameOperation);
		opsList.add(setPostIdOperation);
		opsList.add(setNumLikesOperation);
		opsList.add(setPopularIndexOperation);
		opsList.add(
				Aggregation.sort(Sort.Direction.DESC, POPULAR_INDEX).and(Sort.Direction.DESC, Post.Fields.createdAt));
		opsList.add(Aggregation.skip(skip));
		opsList.add(Aggregation.limit(limit));

		Aggregation aggregation = Aggregation.newAggregation(opsList);

		return mongoTemplate.aggregate(aggregation, MongodbCollectionName.POST, PostRetrDto.class).getMappedResults();
	}

	@Override
	public List<PostRetrDto> getRelatedAllPosts(int skip, int limit, String hookPostId) {
		List<AggregationOperation> opsList = new ArrayList<>();
		final String OWNER_INFO = "ownerInfo";
		final String META_POST_INFO = "metaPostInfo";
		final String LIKE_INFO = "likeInfo";
		final String POPULAR_INDEX = "popularIndex";

		// @formatter:off
 		LookupOperation lookupOwnerInfoOperation = LookupOperation.newLookup()
 				.from(MongodbCollectionName.USER)
 				.localField(Post.Fields.ownerId)
 				.foreignField(User.Fields._id)
 				.as(OWNER_INFO)
 				;
 		LookupOperation lookupMetaPostInfoOperation = LookupOperation.newLookup()
 				.from(MongodbCollectionName.META_POST)
 				.localField(Post.Fields._id)
 				.foreignField(MetaPostList.Fields.postId)
 				.pipeline(Aggregation.match(Criteria.where(MetaPost.Fields.hookPostId).is(new ObjectId(hookPostId))))
 				.as(META_POST_INFO)
 				;
 		LookupOperation lookupLikeInfoOperation = LookupOperation.newLookup()
 				.from(MongodbCollectionName.LIKE)
 				.localField(Post.Fields._id)
 				.foreignField(Like.Fields.postId)
 				.as(LIKE_INFO)
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
		SetOperation setNumLikesOperation = SetOperation
				.set(PostRetrDto.Fields.numLikes)
				.toValue(
					ArrayElemAt.arrayOf(LIKE_INFO + '.' + Like.Fields.numLikes).elementAt(0)
				)
				;
		
		SetOperation setPopularIndexOperation = SetOperation
				.set(POPULAR_INDEX)
				.toValue(
					ArithmeticOperators.Divide.valueOf(
						ArrayElemAt.arrayOf(META_POST_INFO + '.' + MetaPost.Fields.numLikes).elementAt(0)
					).divideBy(
						ArithmeticOperators.Add.valueOf(
							ArrayElemAt.arrayOf(META_POST_INFO + '.' + MetaPost.Fields.numViews).elementAt(0)
						).add(0.00000000001)
					)
				)
				;
 		// @formatter:on

		opsList.add(lookupOwnerInfoOperation);
		opsList.add(lookupMetaPostInfoOperation);
		opsList.add(lookupLikeInfoOperation);
		opsList.add(setOwnerNameOperation);
		opsList.add(setPostIdOperation);
		opsList.add(setNumLikesOperation);
		opsList.add(setPopularIndexOperation);
		opsList.add(
				Aggregation.sort(Sort.Direction.DESC, POPULAR_INDEX).and(Sort.Direction.DESC, Post.Fields.createdAt));
		opsList.add(Aggregation.skip(skip));
		opsList.add(Aggregation.limit(limit));

		Aggregation aggregation = Aggregation.newAggregation(opsList);

		return mongoTemplate.aggregate(aggregation, MongodbCollectionName.POST, PostRetrDto.class).getMappedResults();
	}
}
