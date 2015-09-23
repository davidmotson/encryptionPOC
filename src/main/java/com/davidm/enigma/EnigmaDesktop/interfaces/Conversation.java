package com.davidm.enigma.EnigmaDesktop.interfaces;

import java.util.List;
import java.util.function.Consumer;

import com.davidm.enigma.EnigmaDesktop.domain.Message;
import com.davidm.enigma.EnigmaDesktop.domain.User;

public interface Conversation {
	public User getUser();
	public void addMessageListener(Consumer<String> listener);
	public void clearMessageListeners();
	public void sendMessage(String message);
	public List<Message> getPastMessages();
}
