package com.davidm.enigma.EnigmaDesktop.interfaces;

import java.util.List;
import com.davidm.enigma.EnigmaDesktop.domain.User;

public interface ConversationProvider {
	Conversation getConversation(User with);
	public List<User> getFriends();
	public List<User> getPossibleFriends();
	public void addFriend(User user, long seed, boolean even);
}
