package com.copystagram.api.noti;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotiListDto {
	public int pageNum;
	public int pageSize;
	public List<NotiRetrDto> notifications;
}
