package com.copystagram.api.noticheck;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.copystagram.api.global.config.MongodbCollectionName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Document(collection = MongodbCollectionName.NOTI_CHECK)
@FieldNameConstants
@Getter
@Setter
public class NotiCheck {
	@Id
	public String _id;

	@Field(targetType = FieldType.OBJECT_ID)
	@NotNull(message = "ownerId is required")
	public String ownerId;

	@NotNull(message = "checkedTime is required")
	public LocalDateTime checkedTime;
}
