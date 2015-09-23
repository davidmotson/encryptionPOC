package com.davidm.enigma.EnigmaDesktop.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.inject.Singleton;

import lombok.val;

import com.davidm.enigma.EnigmaDesktop.domain.User;
import com.davidm.enigma.EnigmaDesktop.interfaces.EncryptionManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.KeyManager;
import com.google.inject.Inject;

@Singleton
public class PadEncryptionManager implements EncryptionManager {
	private final KeyManager keyProvider;

	@Inject
	public PadEncryptionManager(KeyManager keyProvider) {
		this.keyProvider = keyProvider;
	}

	@Override
	public String encrypt(String message, User to) {
		val key = keyProvider.getKeyFor(to);
		val rawKey = key.getKey();
		val stringBytes = message.getBytes(StandardCharsets.UTF_8);
		if (stringBytes.length > rawKey.length) {
			throw new RuntimeException("Message is too long");
		}
		val output = new byte[rawKey.length];
		for (int i = 0; i < rawKey.length; i++) {
			if (stringBytes.length <= i) {
				output[i] = rawKey[i];
			} else {
				output[i] = (byte) (rawKey[i] ^ stringBytes[i]);
			}
		}
		return key.getKeyRef() + "::::"
				+ Base64.getEncoder().encodeToString(output);
	}

	@Override
	public String decrypt(String message, User from) {
		val cryptStart = message.indexOf("::::") + 4;
		if(cryptStart == 3){
			return message;
		}
		try {
			val keyRef = Integer.parseInt(message.substring(0, cryptStart - 4));
			val key = keyProvider.rawKeyByRef(keyRef, from);
			val cryptBytes = Base64.getDecoder().decode(
					message.substring(cryptStart));
			val output = new byte[cryptBytes.length];
			for (int i = 0; i < cryptBytes.length; i++) {
				output[i] = (byte) (cryptBytes[i] ^ key[i]);
			}
			return new String(output, StandardCharsets.UTF_8);
		} catch (NumberFormatException e) {
			return message;
		}
	}
}
