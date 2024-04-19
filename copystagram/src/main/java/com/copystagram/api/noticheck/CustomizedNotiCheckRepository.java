package com.copystagram.api.noticheck;

import com.mongodb.client.result.UpdateResult;

public interface CustomizedNotiCheckRepository {
	public UpdateResult upsert(NotiCheck notiCheck);
}
