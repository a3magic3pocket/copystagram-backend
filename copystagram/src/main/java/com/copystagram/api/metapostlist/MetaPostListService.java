package com.copystagram.api.metapostlist;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.copystagram.api.post.PostRepository;
import com.copystagram.api.post.PostRetrDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MetaPostListService {
	public final KafkaTemplate<String, Object> kafkaTemplate;
	public final PostRepository postRepository;

	public void countNumViews(List<PostRetrDto> posts) {
		CompletableFuture.runAsync(() -> {
			System.out.println("inner async start");
			try {
				for (PostRetrDto post : posts) {
					MetaPostList metaPostList = new MetaPostList();
					metaPostList.setNumClicks(0L);
					metaPostList.setNumViews(1L);
					metaPostList.setPostId(post.getPostId());

					this.kafkaTemplate.send("meta-post-list", metaPostList);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			System.out.println("inner async end");
		}).exceptionally((e) -> {
			System.out.println("e: " + e);
			System.out.println("come in exceptionally");

			return null;
		});
	}

	public boolean countNumClicks(String postId) {
		boolean exists = this.postRepository.existsById(postId);
		System.out.println("countNumClicks exists++++" + exists);
		if (exists) {
			MetaPostList metaPostList = new MetaPostList();
			metaPostList.setNumClicks(1L);
			metaPostList.setNumViews(0L);
			metaPostList.setPostId(postId);

			this.kafkaTemplate.send("meta-post-list", metaPostList);

			return true;
		}

		return false;
	}
}
