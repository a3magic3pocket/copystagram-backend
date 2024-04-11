package com.copystagram.api.metapostlist;

import org.apache.kafka.common.serialization.Serdes.WrapperSerde;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class MetaPostListSerde extends WrapperSerde<MetaPostList> {
	public MetaPostListSerde() {
		super(new JsonSerializer<>(), new JsonDeserializer<>(MetaPostList.class));
	}
}
