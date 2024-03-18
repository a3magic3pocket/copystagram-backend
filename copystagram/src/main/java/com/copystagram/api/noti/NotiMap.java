package com.copystagram.api.noti;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;

@Document(collection = MongodbCollectionName.NOTI_MAP)
@Getter
@Setter
public class NotiMap {
	@Id
	public String _id;

	public String code;
	public String locale;
	public String content;
}
