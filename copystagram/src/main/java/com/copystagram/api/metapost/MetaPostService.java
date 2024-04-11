package com.copystagram.api.metapost;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.copystagram.api.post.PostRepository;
import com.copystagram.api.post.PostRetrDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MetaPostService {
	public final KafkaTemplate<String, Object> kafkaTemplate;
	public final PostRepository postRepository;

	public void countNumViews(List<PostRetrDto> posts, String hookPostId) {
		CompletableFuture.runAsync(() -> {
			System.out.println("inner async start");
			try {
				for (PostRetrDto post : posts) {
					MetaPost metaPost = new MetaPost();
					metaPost.setPostId(post.getPostId());
					metaPost.setHookPostId(hookPostId);
					metaPost.setNumLikes(0L);
					metaPost.setNumReplies(0L);
					metaPost.setNumViews(1L);

					this.kafkaTemplate.send("meta-post", metaPost);
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

	private boolean countNumLikes(String postId, String hookPostId, Long numLikes) {
		MetaPost metaPost = new MetaPost();
		metaPost.setPostId(postId);
		metaPost.setHookPostId(hookPostId);
		metaPost.setNumLikes(numLikes);
		metaPost.setNumReplies(0L);
		metaPost.setNumViews(0L);

		this.kafkaTemplate.send("meta-post", metaPost);
		
		return true;
	}

	public boolean upNumLikes(String postId, String hookPostId) {
		return this.countNumLikes(postId, hookPostId, 1L);
	}

	public boolean downNumLikes(String postId, String hookPostId) {
		return this.countNumLikes(postId, hookPostId, -1L);
	}
}
