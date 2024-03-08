package com.copystagram.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaSampleProducerService {
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(String message) {
		System.out.println("send Message " + message);
		this.kafkaTemplate.send("post-create", message);
	}

}
