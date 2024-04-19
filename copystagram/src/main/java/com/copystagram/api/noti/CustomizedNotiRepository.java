package com.copystagram.api.noti;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomizedNotiRepository {
	public List<NotiRetrDto> getLatestNotis(int skip, int limit, String onwerId);

	public List<Noti> getMyUncheckedNotis(int skip, int limit, String onwerId, LocalDateTime notiCheckedTime);
}
