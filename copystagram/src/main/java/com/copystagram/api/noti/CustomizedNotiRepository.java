package com.copystagram.api.noti;

import java.util.List;

public interface CustomizedNotiRepository {
	public List<NotiRetrDto> getLatestNotis(int skip, int limit, String id);
}
