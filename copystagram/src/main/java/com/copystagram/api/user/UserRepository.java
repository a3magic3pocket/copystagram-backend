package com.copystagram.api.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<User, String> {
	public User findByEmail(String email);
	public User findByName(String name);
}
