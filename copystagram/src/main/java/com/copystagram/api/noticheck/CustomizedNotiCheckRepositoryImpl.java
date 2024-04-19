package com.copystagram.api.noticheck;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.UpdateResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomizedNotiCheckRepositoryImpl implements CustomizedNotiCheckRepository {
	public final MongoTemplate mongoTemplate;

	@Override
	public UpdateResult upsert(NotiCheck notiCheck) {
		Query query = new Query();
		Criteria criteria = new Criteria();

		// @formatter:off
		criteria.andOperator(
			Criteria.where(NotiCheck.Fields.ownerId).is(notiCheck.getOwnerId())
		);
		// @formatter:one
		query.addCriteria(criteria);
		Update update = new Update();

		update.set(NotiCheck.Fields.checkedTime, notiCheck.getCheckedTime());

		return this.mongoTemplate.upsert(query, update, NotiCheck.class);
	}
}
