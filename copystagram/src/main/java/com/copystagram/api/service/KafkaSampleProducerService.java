package com.copystagram.api.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KafkaSampleProducerService {
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(String message) {
		System.out.println("send Message " + message);
		this.kafkaTemplate.send("expt", message);
	}

}
