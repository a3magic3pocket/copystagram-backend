package com.copystagram.api.like;

import org.springframework.stereotype.Service;

import com.copystagram.api.metapost.MetaPostService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LikeService {
	public final LikeRepository likeRepository;
	public final MetaPostService metaPostService;

	public void up(LikeUpsertDto likeUpsertDto, String hookPostId) {
		Like like = new Like();
		like.setPostId(likeUpsertDto.getPostId());
		like.setOwnerId(likeUpsertDto.getOwnerId());
		like.setNumLikes(Long.valueOf(1));

		this.likeRepository.upsert(like);

		if (hookPostId != null) {
			this.metaPostService.upNumLikes(likeUpsertDto.getPostId(), hookPostId);
		}
	}

	public void down(LikeUpsertDto likeUpsertDto, String hookPostId) {
		Like like = new Like();
		like.setPostId(likeUpsertDto.getPostId());
		like.setOwnerId(likeUpsertDto.getOwnerId());
		like.setNumLikes(Long.valueOf(0));

		this.likeRepository.upsert(like);

		if (hookPostId != null) {
			this.metaPostService.downNumLikes(likeUpsertDto.getPostId(), hookPostId);
		}
	}
}
