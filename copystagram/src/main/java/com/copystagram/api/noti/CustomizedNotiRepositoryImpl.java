package com.copystagram.api.noti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.SetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.copystagram.api.global.config.MongodbCollectionName;
import com.copystagram.api.post.Post;
import com.copystagram.api.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomizedNotiRepositoryImpl implements CustomizedNotiRepository {
	public final MongoTemplate mongoTemplate;

	private List<NotiRetrDto> getLatestNotisLogic(int skip, int limit, List<Criteria> criteriaList) {
		List<AggregationOperation> opsList = new ArrayList<>();
		final String OWNER_INFO = "ownerInfo";
		final String POST_INFO = "postInfo";

		for (Criteria criteria : criteriaList) {
			opsList.add(Aggregation.match(criteria));
		}

		// @formatter:off
 		LookupOperation ownerLookupOperation = LookupOperation.newLookup()
 				.from(MongodbCollectionName.USER)
 				.localField(Noti.Fields.ownerId)
 				.foreignField(User.Fields._id)
 				.as(OWNER_INFO)
 				;
		SetOperation ownerNameSetOperation = SetOperation
				.set(NotiRetrDto.Fields.ownerName)
				.toValue(ArrayElemAt.arrayOf(OWNER_INFO + '.' + User.Fields.name)
				.elementAt(0))
				;
		SetOperation ownerImageSetOperation = SetOperation
				.set(NotiRetrDto.Fields.ownerThumbImagePath)
				.toValue(ArrayElemAt.arrayOf(OWNER_INFO + '.' + User.Fields.userImagePath)
				.elementAt(0))
				;
 		LookupOperation postLookupOperation = LookupOperation.newLookup()
 				.from(MongodbCollectionName.POST)
 				.localField(Noti.Fields.relatedPostId)
 				.foreignField(Post.Fields._id)
 				.as(POST_INFO)
 				;
		SetOperation postSetOperation = SetOperation
				.set(NotiRetrDto.Fields.postThumbImagePath)
				.toValue(ArrayElemAt.arrayOf(POST_INFO + '.' + Post.Fields.thumbImagePath)
				.elementAt(0))
				;
 		// @formatter:on

		opsList.add(ownerLookupOperation);
		opsList.add(ownerNameSetOperation);
		opsList.add(ownerImageSetOperation);
		opsList.add(postLookupOperation);
		opsList.add(postSetOperation);
		opsList.add(Aggregation.sort(Sort.Direction.DESC, Noti.Fields.createdAt));
		opsList.add(Aggregation.skip(skip));
		opsList.add(Aggregation.limit(limit));

		Aggregation aggregation = Aggregation.newAggregation(opsList);

		return this.mongoTemplate.aggregate(aggregation, MongodbCollectionName.NOTI, NotiRetrDto.class)
				.getMappedResults();
	}

	@Override
	public List<NotiRetrDto> getLatestNotis(int skip, int limit, String onwerId) {
		List<Criteria> criteriaList = List.of(Criteria.where(Noti.Fields.ownerId).is(new ObjectId(onwerId)));

		return this.getLatestNotisLogic(skip, limit, criteriaList);
	}

	@Override
	public List<Noti> getMyUncheckedNotis(int skip, int limit, String ownerId, LocalDateTime notiCheckedTime) {
		Query query = new Query();
		Criteria criteria = new Criteria();

		// @formatter:off
		criteria.andOperator(
			Criteria.where(Noti.Fields.ownerId).is(ownerId),
			Criteria.where(Noti.Fields.createdAt).gt(notiCheckedTime)
		);
		// @formatter:on
		query.addCriteria(criteria);

		return this.mongoTemplate.find(query, Noti.class);
	}
}
