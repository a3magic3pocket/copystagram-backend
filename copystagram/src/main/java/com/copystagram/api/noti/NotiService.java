package com.copystagram.api.noti;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.copystagram.api.global.encryption.HashUtil;
import org.springframework.dao.DuplicateKeyException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotiService {
	public final NotiMapRepository notiMapRepository;
	public final NotiRepository notiRepository;
	public final HashUtil hashUtil;
	public final KafkaTemplate<String, Object> kafkaTemplate;

	public boolean createNoti(String to, String code) {
		NotiMap notiMap = notiMapRepository.findByCode(code);
		if (notiMap == null) {
			System.out.println("createNoti::code not exists+" + code);
			return false;
		}

		NotiCreationKafkaDto notiCreationKafkaDto = new NotiCreationKafkaDto();
		notiCreationKafkaDto.setContent(notiMap.content);
		notiCreationKafkaDto.setOwnerId(to);
		notiCreationKafkaDto.setCreatedAt(LocalDateTime.now());

		this.kafkaTemplate.send("noti-creation", notiCreationKafkaDto);
		return true;
	}

	@KafkaListener(topics = "noti-creation", groupId = "noti-creation", containerFactory = "notiCreationKafkaListener")
	private void consumeNotiCreation(NotiCreationKafkaDto message, Acknowledgment acknowledgment) {
		try {
			String content = message.getContent();
			String ownerId = message.getOwnerId();
			LocalDateTime createdAt = message.getCreatedAt();
			String source = content + ownerId + createdAt;

			Noti noti = new Noti();
			noti.setContent(content);
			noti.setOwnerId(ownerId);
			noti.setCreateAt(createdAt);
			noti.setDocHash(hashUtil.getSha256Hash(source));
			notiRepository.save(noti);
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
}
