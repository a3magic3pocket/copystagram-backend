package com.copystagram.api.metapost;

import org.apache.kafka.common.serialization.Serdes.WrapperSerde;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class MetaPostSerde extends WrapperSerde<MetaPost> {
	public MetaPostSerde() {
		super(new JsonSerializer<>(), new JsonDeserializer<>(MetaPost.class));
	}
}
