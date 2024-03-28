package com.copystagram.api.streams;

import org.apache.kafka.common.serialization.Serdes.WrapperSerde;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class PostInfoSerde extends WrapperSerde<PostInfo> {
	public PostInfoSerde() {
		super(new JsonSerializer<>(), new JsonDeserializer<>(PostInfo.class));
	}
}
