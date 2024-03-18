package com.copystagram.api.noti;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotiRepository extends MongoRepository<Noti, String> {
}
