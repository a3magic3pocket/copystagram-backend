package com.copystagram.api.metapostlist;

import org.springframework.data.mongodb.core.mapping.Document;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Document(collection = MongodbCollectionName.META_POST_LIST)
@FieldNameConstants
@Getter
@Setter
public class MetaPostList {
	public String postId;
	public Long numClicks;
	public Long numViews;
}
