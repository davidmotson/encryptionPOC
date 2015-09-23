package com.davidm.enigma.EnigmaDesktop.interfaces;

import com.davidm.enigma.EnigmaDesktop.domain.User;

public interface EncryptionManager {
	public String encrypt(String message, User to);
	public String decrypt(String message, User from);
}
