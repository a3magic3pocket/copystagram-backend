package com.copystagram.api.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaSampleConsumerService {

	@KafkaListener(topics = "post-create", groupId = "post-create")
	public void consume(String message) throws java.io.IOException {
		System.out.println("receive message: " + message);
	}

}
