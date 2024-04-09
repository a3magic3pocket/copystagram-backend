package com.copystagram.api.like;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LikeService {
	public final LikeRepository likeRepository;

	public void up(LikeUpsertDto likeUpsertDto) {
		Like like = new Like();
		like.setPostId(likeUpsertDto.getPostId());
		like.setOwnerId(likeUpsertDto.getOwnerId());
		like.setNumLikes(Long.valueOf(1));

		this.likeRepository.upsert(like);
	}

	public void down(LikeUpsertDto likeUpsertDto) {
		Like like = new Like();
		like.setPostId(likeUpsertDto.getPostId());
		like.setOwnerId(likeUpsertDto.getOwnerId());
		like.setNumLikes(Long.valueOf(0));

		this.likeRepository.upsert(like);
	}
}
