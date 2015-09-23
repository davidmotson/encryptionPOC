package com.davidm.enigma.EnigmaDesktop.interfaces;

import java.util.Set;

import com.davidm.enigma.EnigmaDesktop.domain.User;

public interface ConversationProvider {
	Conversation getConversation(User with);
	public Set<User> getFriends();
	public void addFriend(User user, long seed, boolean even);
}
