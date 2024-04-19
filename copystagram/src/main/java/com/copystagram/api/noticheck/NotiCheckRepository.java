package com.copystagram.api.noticheck;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotiCheckRepository extends MongoRepository<NotiCheck, String>, CustomizedNotiCheckRepository {
	public NotiCheck findByOwnerId(String ownerId);
}
