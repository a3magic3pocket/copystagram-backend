package com.copystagram.api.noticheck;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotiCheckService {
	public final NotiCheckRepository notiCheckRepository;

	public boolean upsert(String ownerId) {
		NotiCheck notiCheck = new NotiCheck();
		notiCheck.setOwnerId(ownerId);
		notiCheck.setCheckedTime(LocalDateTime.now());

		this.notiCheckRepository.upsert(notiCheck);

		return true;
	}
}
