package com.copystagram.api.noti;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Getter
@Setter
public class NotiRetrDto {
	public String ownerName;
	public String ownerThumbImagePath;
	public String content;
	public String relatedPostId;
	public String postThumbImagePath;
	public LocalDateTime createdAt;
}
