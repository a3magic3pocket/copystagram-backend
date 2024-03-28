package com.copystagram.api.global.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import com.copystagram.api.streams.PostInfo;
import com.copystagram.api.streams.PostInfoSerde;

@Configuration
public class KafkaStreamsConfig {

	@Value("${spring.kafka.producer.bootstrap-servers}")
	private String bootstrapServers;

	KafkaStreamsConfiguration kStreamsConfig(String applicationId, Object valueSerde) {
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, valueSerde);

		return new KafkaStreamsConfiguration(props);
	}

	@Bean("postInfoDSLBuilder")
	FactoryBean<StreamsBuilder> postInfoDSLBuilder() {
		StreamsBuilderFactoryBean streamsBuilder = new StreamsBuilderFactoryBean(
				kStreamsConfig("post-info", PostInfoSerde.class));
		return streamsBuilder;
	}

	@Bean("postInfoKStream")
	KStream<String, PostInfo> postInfoKStream(@Qualifier("postInfoDSLBuilder") StreamsBuilder postInfoDSLBuilder) {
		KStream<String, PostInfo> kStream = postInfoDSLBuilder.stream("post-info");
		System.out.println("come in here?");

		// @formatter:off
		kStream
			.filter((key, value)-> value.getPostId() != null)
			.selectKey((key, value) -> {
				System.out.println("come in here???222");
				return value.getPostId().replace("\"", "");
			})
			.groupByKey()
			.aggregate(
				new Initializer<PostInfo>() {
					public PostInfo apply() {
						return new PostInfo();
					}
				},
				new Aggregator<String, PostInfo, PostInfo>() {
					public PostInfo apply(String key, PostInfo value, PostInfo aggregate) {
						aggregate.setPostId(value.getPostId());
						
						aggregate.setNumExposed(
							aggregate.getNumExposed() + value.getNumExposed()
						);
						
						aggregate.setNumLike(
							aggregate.getNumLike() + value.getNumLike()
						);
						
						aggregate.setNumReply(
								aggregate.getNumReply() + value.getNumReply()
						);
						
						System.out.println("aggregate" + aggregate.toString());
						
						return aggregate;
					}
				},
				Materialized.as("PostInfo")
			)
		.toStream()
		.to("post-info-result");
		return kStream;
	}
}
