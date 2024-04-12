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

import com.copystagram.api.metapost.MetaPost;
import com.copystagram.api.metapost.MetaPostSerde;
import com.copystagram.api.metapostlist.MetaPostList;
import com.copystagram.api.metapostlist.MetaPostListSerde;

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

	private String formatKTableKey(String key) {
		return "{'_id':\"" + key + "\"}";
	}

	@Bean("metaPostListDSLBuilder")
	FactoryBean<StreamsBuilder> metaPostListDSLBuilder() {
		StreamsBuilderFactoryBean streamsBuilder = new StreamsBuilderFactoryBean(
				kStreamsConfig("meta-post-list", MetaPostListSerde.class));
		return streamsBuilder;
	}

	@Bean("metaPostListKStream")
	KStream<String, MetaPostList> metaPostListKStream(
			@Qualifier("metaPostListDSLBuilder") StreamsBuilder metaPostListDSLBuilder) {
		KStream<String, MetaPostList> kStream = metaPostListDSLBuilder.stream("meta-post-list");
		System.out.println("IN meta-post-list");

		// @formatter:off
		kStream
			.filter((key, value) -> value.getPostId() != null && value.getNumClicks() != null && value.getNumViews() != null)
			.selectKey((key, value) -> {
				String businessKey = value.getPostId();
				return this.formatKTableKey(businessKey);
			})
			.groupByKey()
			.aggregate(
				new Initializer<MetaPostList>() {
					public MetaPostList apply() {
						return new MetaPostList();
					}
				},
				new Aggregator<String, MetaPostList, MetaPostList>() {
					public MetaPostList apply(String key, MetaPostList value, MetaPostList aggregate) {
						System.out.println("value.getPostId()++" + value.getPostId());
						System.out.println("value.getNumViews()++" + value.getNumViews());
						System.out.println("value.getNumClicks()++" + value.getNumClicks());
						
						aggregate.setPostId(value.getPostId());
						
						Long newNumViews = aggregate.getNumViews() == null ? 
								value.getNumViews() : 
								aggregate.getNumViews() + value.getNumViews()
						;
						aggregate.setNumViews(newNumViews);

						Long newNumClicks = aggregate.getNumClicks() == null ? 
								value.getNumClicks() : 
								aggregate.getNumClicks() + value.getNumClicks()
						;
						aggregate.setNumClicks(newNumClicks);
						
						
						return aggregate;
					}
				},
				Materialized.as(MongodbCollectionName.META_POST_LIST)
			)
		.toStream()
		.to("meta-post-list-result");
		return kStream;
	}
	
	@Bean("metaPostDSLBuilder")
	FactoryBean<StreamsBuilder> metaPostDSLBuilder() {
		StreamsBuilderFactoryBean streamsBuilder = new StreamsBuilderFactoryBean(
				kStreamsConfig("meta-post", MetaPostSerde.class));
		return streamsBuilder;
	}
	
	@Bean("metaPostKStream")
	KStream<String, MetaPost> metaPostKStream(
			@Qualifier("metaPostDSLBuilder") StreamsBuilder metaPostDSLBuilder) {
		KStream<String, MetaPost> kStream = metaPostDSLBuilder.stream("meta-post");
		System.out.println("IN meta-post");

		// @formatter:off
		kStream
			.filter((key, value) -> (
				value.getPostId() != null &&
				value.getHookPostId() != null &&
				value.getNumLikes() != null &&
				value.getNumReplies() != null && 
				value.getNumViews() != null
			))
			.selectKey((key, value) -> {
				String businessKey = value.getPostId() + "|" + value.getHookPostId();
				return this.formatKTableKey(businessKey);
			})
			.groupByKey()
			.aggregate(
				new Initializer<MetaPost>() {
					public MetaPost apply() {
						return new MetaPost();
					}
				},
				new Aggregator<String, MetaPost, MetaPost>() {
					public MetaPost apply(String key, MetaPost value, MetaPost aggregate) {
						System.out.println("value.getPostId()" + value.getPostId());
						System.out.println("value.getHookPostId()" + value.getHookPostId());
						System.out.println("value.getNumLikes()" + value.getNumLikes());
						System.out.println("value.getNumReplies()" + value.getNumReplies());
						System.out.println("value.getNumViews()" + value.getNumViews());
						
						aggregate.setPostId(value.getPostId());
						aggregate.setHookPostId(value.getHookPostId());
						
						Long newNumLikes = aggregate.getNumLikes() == null ? 
							value.getNumLikes() : 
							aggregate.getNumLikes() + value.getNumLikes()
						;
						aggregate.setNumLikes(newNumLikes);

						Long newNumReplies = aggregate.getNumReplies() == null ? 
							value.getNumReplies() : 
							aggregate.getNumReplies() + value.getNumReplies()
						;
						aggregate.setNumReplies(newNumReplies);

						Long newNumViews = aggregate.getNumViews() == null ? 
							value.getNumViews() : 
							aggregate.getNumViews() + value.getNumViews()
						;
						aggregate.setNumViews(newNumViews);
						
						return aggregate;
					}
				},
				Materialized.as(MongodbCollectionName.META_POST)
			)
		.toStream()
		.to("meta-post-result");
		return kStream;
	}
}
