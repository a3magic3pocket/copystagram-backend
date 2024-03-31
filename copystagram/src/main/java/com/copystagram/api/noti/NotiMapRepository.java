package com.copystagram.api.noti;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotiMapRepository extends MongoRepository<NotiMap, String>, CustomizedNotiRepository {
	public NotiMap findByCode(String code);
}
