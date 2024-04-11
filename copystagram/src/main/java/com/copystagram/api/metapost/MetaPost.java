package com.copystagram.api.metapost;

import org.springframework.data.mongodb.core.mapping.Document;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Document(collection = MongodbCollectionName.META_POST)
@FieldNameConstants
@Getter
@Setter
public class MetaPost {
	public String postId;
	public String hookPostId;
	public Long numViews;
	public Long numLikes;
	public Long numReplies;
}
