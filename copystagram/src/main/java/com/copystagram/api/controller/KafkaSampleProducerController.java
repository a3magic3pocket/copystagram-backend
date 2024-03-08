package com.copystagram.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.copystagram.api.dto.TestDto;
import com.copystagram.api.service.KafkaSampleProducerService;

@RestController
public class KafkaSampleProducerController {

	@Autowired
	private KafkaSampleProducerService kafkaSampleProducerService;

	@PostMapping(value = "/sendMessage")
	public void sendMessage(@RequestBody TestDto testDto) {
		kafkaSampleProducerService.sendMessage(testDto.getMessage());
	}

}
