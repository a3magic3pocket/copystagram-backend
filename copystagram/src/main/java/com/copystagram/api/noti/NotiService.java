package com.copystagram.api.noti;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.copystagram.api.global.encryption.HashUtil;
import com.copystagram.api.noticheck.NotiCheck;
import com.copystagram.api.noticheck.NotiCheckRepository;

import org.springframework.dao.DuplicateKeyException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotiService {
	public final NotiMapRepository notiMapRepository;
	public final NotiCheckRepository notiCheckRepository;
	public final NotiRepository notiRepository;
	public final HashUtil hashUtil;
	public final KafkaTemplate<String, Object> kafkaTemplate;

	public boolean create(String to, String code, String relatedPostId) {
		NotiMap notiMap = this.notiMapRepository.findByCode(code);
		if (notiMap == null) {
			System.out.println("createNoti::code not exists+" + code);
			return false;
		}

		NotiCreationKafkaDto notiCreationKafkaDto = new NotiCreationKafkaDto();
		notiCreationKafkaDto.setContent(notiMap.getContent());
		notiCreationKafkaDto.setOwnerId(to);
		notiCreationKafkaDto.setRelatedPostId(relatedPostId);
		notiCreationKafkaDto.setCreatedAt(LocalDateTime.now());

		this.kafkaTemplate.send("noti-creation", notiCreationKafkaDto);
		return true;
	}

	@KafkaListener(topics = "noti-creation", groupId = "noti-creation", containerFactory = "notiCreationKafkaListener")
	private void consumeNotiCreation(NotiCreationKafkaDto message, Acknowledgment acknowledgment) {
		try {
			String content = message.getContent();
			String ownerId = message.getOwnerId();
			String relatedPostId = message.getRelatedPostId();
			LocalDateTime createdAt = message.getCreatedAt();
			String source = content + ownerId + relatedPostId + createdAt;

			Noti noti = new Noti();
			noti.setContent(content);
			noti.setOwnerId(ownerId);
			noti.setCreatedAt(createdAt);
			noti.setRelatedPostId(relatedPostId);
			noti.setContent(content);
			noti.setDocHash(hashUtil.getSha256Hash(source));
			this.notiRepository.save(noti);
			acknowledgment.acknowledge();
		} catch (DuplicateKeyException e) {
			// pass
			acknowledgment.acknowledge();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("NotiService.consumeNotiCreation e: " + e);
			acknowledgment.nack(Duration.ofMinutes(4));
		}
	}

	public NotiListDto getLatestNotis(int pageNum, int pageSize, String onwerId) {
		int skip = (pageNum - 1) * pageSize;

		List<NotiRetrDto> notis = this.notiRepository.getLatestNotis(skip, pageSize, onwerId);

		NotiListDto notiListDto = new NotiListDto();
		notiListDto.setPageNum(pageNum);
		notiListDto.setPageSize(notis.size());
		notiListDto.setNotifications(notis);

		return notiListDto;
	}

	public List<String> getMyUncheckedNotis(int pageNum, int pageSize, String onwerId) {
		LocalDateTime notiCheckeDate;
		NotiCheck notiCheck = this.notiCheckRepository.findByOwnerId(onwerId);
		if (notiCheck == null) {
			// All notis have not been checked
			notiCheckeDate = LocalDate.of(1899, Month.JANUARY, 1).atStartOfDay();
		} else {
			notiCheckeDate = notiCheck.getCheckedTime();
		}

		int skip = (pageNum - 1) * pageSize;
		System.out.println("notiCheckeDate+++" + notiCheckeDate);

		List<Noti> notis = this.notiRepository.getMyUncheckedNotis(skip, pageSize, onwerId, notiCheckeDate);
		System.out.println("notis++" + notis);
		List<String> notiIds = new ArrayList<String>();
		for (Noti noti : notis) {
			notiIds.add(noti.get_id());
		}

		return notiIds;
	}
}
