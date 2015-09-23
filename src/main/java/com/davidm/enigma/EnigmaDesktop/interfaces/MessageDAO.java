package com.davidm.enigma.EnigmaDesktop.interfaces;

import java.util.List;

import com.davidm.enigma.EnigmaDesktop.domain.Message;
import com.davidm.enigma.EnigmaDesktop.domain.User;

public interface MessageDAO {
	public List<Message> getAllMessagesFor(User with);
	public void addMessage(String message, User with, boolean sent);
}
