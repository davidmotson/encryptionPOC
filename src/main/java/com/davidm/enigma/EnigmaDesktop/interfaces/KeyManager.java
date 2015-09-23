package com.davidm.enigma.EnigmaDesktop.interfaces;

import com.davidm.enigma.EnigmaDesktop.domain.Key;
import com.davidm.enigma.EnigmaDesktop.domain.User;

public interface KeyManager {
	void generateKey(User toAddress, long seed, boolean even);
	Key getKeyFor(User toAddress);
	byte[] rawKeyByRef(int ref, User fromAddress);
}
