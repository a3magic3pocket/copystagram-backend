package com.copystagram.api.global.encryption;

import java.security.MessageDigest;

import org.springframework.stereotype.Component;

@Component
public class HashUtil {
	public byte[] getSha256Hash(String phrase) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return digest.digest(phrase.getBytes());
		} catch (Exception e) {
			return phrase.getBytes();
		}
	}
}
