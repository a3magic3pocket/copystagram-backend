package com.copystagram.api.global.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.copystagram.api.noti.NotiCreationKafkaDto;
import com.copystagram.api.post.PostCreationKafkaDto;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.producer.bootstrap-servers}")
	private String producerBootstrapServers;

	@Value("${spring.kafka.consumer.bootstrap-servers}")
	private String consumerBootstrapServers;

	ConsumerFactory<String, Object> consumerFactory(String valueDefaultType) {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrapServers);
		configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
		configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueDefaultType);

		return new DefaultKafkaConsumerFactory<String, Object>(configProps);
	}

	ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		return new DefaultKafkaProducerFactory<String, Object>(configProps);
	}

	@Bean
	KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, PostCreationKafkaDto> postCreationKafkaListener() {
		ConcurrentKafkaListenerContainerFactory<String, PostCreationKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory("com.copystagram.api.post.PostCreationKafkaDto"));
		return factory;
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, NotiCreationKafkaDto> notiCreationKafkaListener() {
		ConcurrentKafkaListenerContainerFactory<String, NotiCreationKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory("com.copystagram.api.noti.NotiCreationKafkaDto"));
		return factory;
	}
}