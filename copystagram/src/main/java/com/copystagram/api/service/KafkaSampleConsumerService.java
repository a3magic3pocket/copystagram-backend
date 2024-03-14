package com.copystagram.api.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaSampleConsumerService {

	@KafkaListener(topics = "expt", groupId = "expt")
	public void consume(String message) throws java.io.IOException {
		System.out.println("receive message: " + message);
	}

}
